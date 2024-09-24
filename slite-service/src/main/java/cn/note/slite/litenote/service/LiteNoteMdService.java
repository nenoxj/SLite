package cn.note.slite.litenote.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.swing.core.filestore.RelativeFileStore;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Markdown文件转换
 * 支持格式
 * ### git
 * <p>
 * ```
 * # 添加代码
 * git add .
 * ```
 * <p>
 * ```
 * # 拉取代码
 * git pull origin develop
 * ```
 */
@Slf4j
public class LiteNoteMdService {
    private static final String SUFFIX = ".md";

    private RelativeFileStore fileStore;

    public LiteNoteMdService(RelativeFileStore fileStore) throws IOException {
        this.fileStore = fileStore;
    }


    public List<LiteNote> listAll() throws IOException {
        List<LiteNote> liteNoteList = new ArrayList<>(100);
        fileStore.lists(FileFilterUtils.suffixFileFilter(SUFFIX), CollUtil.newArrayList(".history"), path -> {
            try {
                File file = path.toFile();
                if (file.isFile()) {
                    List<LiteNote> mdList = md2LiteNote(file);
                    if (mdList != null) {
                        liteNoteList.addAll(mdList);
                    }
                }
            } catch (IOException e) {
                log.info("读取异常:{}", e.getMessage());
            }
        });
        return liteNoteList;
    }


    private List<LiteNote> md2LiteNote(File mdFile) throws IOException {
        String content = fileStore.readFile(mdFile);
        if (StrUtil.isBlank(content)) {
            return null;
        }

        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        Parser parser = Parser.builder(options).build();
        Node node = parser.parse(content);
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        String htmlContent = renderer.render(node);
        Document document = Jsoup.parse(htmlContent);
        String group = document.selectFirst("h3").text();
        Elements elements = document.select("pre");
        List<LiteNote> liteNoteList = new ArrayList<>(elements.size());
        Console.log("file==>{},code size ==>{}", mdFile, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            LiteNote liteNote = new LiteNote();
            liteNote.setId(mdFile.getName() + "@" + i);
            liteNote.setGroups(group);
            liteNote.setContent(element.text());
            liteNoteList.add(liteNote);
        }
        return liteNoteList;
    }

}
