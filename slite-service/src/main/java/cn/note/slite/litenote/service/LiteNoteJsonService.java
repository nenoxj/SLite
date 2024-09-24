package cn.note.slite.litenote.service;

import cn.hutool.json.JSONUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.swing.core.filestore.RelativeFileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * json存储
 * 更新笔记及创建索引
 */
@Slf4j
public class LiteNoteJsonService {

    private static final String SUFFIX = ".slite.json";

    private RelativeFileStore fileStore;

    public LiteNoteJsonService(RelativeFileStore fileStore) throws IOException {
        this.fileStore = fileStore;
    }

    public void batchSave(List<LiteNote> liteNoteList) throws IOException {
        for (LiteNote liteNote : liteNoteList) {
            String id = liteNote.getId();
            String fileName = id.concat(SUFFIX);
            fileStore.writeRelativeFile(fileName, JSONUtil.toJsonStr(liteNote));
        }
    }

    public void saveOrUpdate(LiteNote liteNote) throws IOException {
        // 写入本地
        String id = liteNote.getId();
        String fileName = id.concat(SUFFIX);
        fileStore.writeRelativeFile(fileName, JSONUtil.toJsonStr(liteNote));
    }

    public void deleteById(String id) throws IOException {
        String fileName = id.concat(SUFFIX);
        fileStore.deleteRelativeFile(fileName);
    }

    public List<LiteNote> listAll() throws IOException {
        List<LiteNote> liteNoteList = new ArrayList<>(100);
        fileStore.lists(FileFilterUtils.suffixFileFilter(SUFFIX), null, path -> {
            try {
                File file = path.toFile();
                if (file.isFile()) {
                    String content = fileStore.readFile(file);
                    liteNoteList.add(JSONUtil.toBean(content, LiteNote.class));
                }
            } catch (IOException e) {
                log.info("读取异常:{}", e.getMessage());
            }
        });
        return liteNoteList;
    }

}
