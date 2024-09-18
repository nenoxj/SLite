package cn.note.slite.litenote.service;

import cn.hutool.core.lang.Console;
import cn.note.slite.core.entity.LiteNote;
import cn.note.swing.core.util.CmdUtil;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 综合测试
 */
public class LiteNoteLuceneServiceCmdTest {

    private static LiteNoteLuceneService liteNoteLuceneService;

    public static void init() throws IOException {
        Path path = Paths.get("D:\\lucene-test", "notes-cmd");
        liteNoteLuceneService = new LiteNoteLuceneService(FSDirectory.open(path));
    }

    public static void main(String[] args) throws IOException {
        init();
//        queryTest();
        allTest();
    }


    public static void queryTest() throws IOException {
        CmdUtil.show(cmd -> {
            try {
                List<LiteNote> liteNotes = liteNoteLuceneService.search(cmd, 10);
                if (liteNotes.isEmpty()) {
                    Console.log("未匹配到结果!");
                } else {
                    liteNotes.forEach(Console::log);
                }
            } catch (IOException e) {
                Console.log("匹配异常!");
            }
        });
    }

    public static void allTest() throws IOException {
        CmdUtil.show(cmd -> {
            try {
                if (cmd.startsWith("+ ")) {
                    LiteNote liteNote = new LiteNote();
                    String content = cmd.substring(2);
                    liteNote.setContent(content);
                    liteNoteLuceneService.saveOrUpdate(liteNote);
                } else if (cmd.startsWith("-")) {
                    liteNoteLuceneService.deleteAll();
                } else {
                    List<LiteNote> liteNotes = liteNoteLuceneService.search(cmd, 10);
                    if (liteNotes.isEmpty()) {
                        Console.log("未匹配到结果!");
                    } else {
                        liteNotes.forEach(Console::log);
                    }
                }
            } catch (IOException e) {
                Console.log("匹配异常!");
            }
        });


    }

}