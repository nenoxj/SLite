package cn.note.slite.litenote.service;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;
import cn.note.swing.core.util.CmdUtil;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 综合测试
 */
public class LiteNoteHardDiskServiceCmdTest {

    private static LiteNoteHardDiskService liteNoteHardDiskService;

    public static void init() throws IOException {
        File dataDir = FileUtils.getFile("D:/lucene-test", "disk-test");
        File indexDir = FileUtils.getFile("D:/lucene-test", "disk-index");
        liteNoteHardDiskService = new LiteNoteHardDiskService(dataDir,indexDir);
    }

    public static void main(String[] args) throws IOException {
        init();
        pageTest();
    }


    public static void pageTest() throws IOException {
        // p:1,10>s:note
        CmdUtil.show(cmd -> {
            try {
                int currentPage = 1;
                int pageSize = 10;
                if (cmd.startsWith("p:")) {
                    String pageParam = StrUtil.subBetween(cmd, "p:", ">");
                    String[] pageArray = StrUtil.split(pageParam, ",");
                    if (pageArray.length == 2) {
                        currentPage = Integer.parseInt(pageArray[0]);
                        pageSize = Integer.parseInt(pageArray[1]);
                    }
                }
                if (cmd.contains("s:")) {
                    String searchText = StrUtil.subAfter(cmd, "s:", false);
                    Page<LiteNote> page = liteNoteHardDiskService.searchPage(searchText, currentPage, pageSize);
                    Console.log("total:{},currentPage:{},pageSize:{}", page.getTotalCount(), currentPage, pageSize);
                    page.getList().forEach(liteNote -> {
                        Console.log(JSONUtil.toJsonStr(liteNote));
                    });
                    StaticLog.info("search end!");
                }
            } catch (IOException e) {
                Console.log("匹配异常!");
            }
        });
    }


}