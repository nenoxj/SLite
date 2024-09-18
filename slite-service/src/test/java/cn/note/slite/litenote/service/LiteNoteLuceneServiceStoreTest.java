package cn.note.slite.litenote.service;

import cn.hutool.core.lang.Console;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.util.CallbackHelper;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
/**
 * 存储测试
 */
public class LiteNoteLuceneServiceStoreTest {

    private LiteNoteLuceneService liteNoteLuceneService;

    private List<LiteNote> liteNoteList;

    @Before
    public void before() throws IOException {
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\lucene-test", "notes-store"));
        liteNoteLuceneService = new LiteNoteLuceneService(fsDirectory);
    }

    @Test
    public void deleteAll() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            liteNoteLuceneService.deleteAll();
        });

    }

    @Test
    public void batchAdd() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            IntStream.range(0, 100000).forEach(i -> {
                LiteNoteHelper.createLiteNote("content" + i, "keywords" + i);
            });
            liteNoteLuceneService.batchSave(LiteNoteHelper.liteNoteList);
            Console.log("indices size==>{}", liteNoteLuceneService.count());
        });

    }

    @Test
    public void querySize() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            liteNoteLuceneService.queryList(new MatchAllDocsQuery(), 20).forEach(liteNote -> {
                Console.log("search note==>{}", liteNote);
            });
        });
    }


}