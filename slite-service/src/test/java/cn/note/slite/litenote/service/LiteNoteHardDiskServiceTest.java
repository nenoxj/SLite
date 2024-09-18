package cn.note.slite.litenote.service;

import cn.hutool.core.lang.Console;
import cn.note.slite.core.entity.LiteNote;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class LiteNoteHardDiskServiceTest {


    private LiteNoteHardDiskService liteNoteHardDiskService;

    @Before
    public void init() throws IOException {
        File dataDir = FileUtils.getFile("D:/lucene-test", "disk-test");
        File indexDir = FileUtils.getFile("D:/lucene-test", "disk-index");
        liteNoteHardDiskService = new LiteNoteHardDiskService(dataDir,indexDir);
    }

    @Test
    public void batchSave() throws IOException {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            LiteNoteHelper.createLiteNote("test note" + i, "my");

        });
        liteNoteHardDiskService.batchSave(LiteNoteHelper.liteNoteList);
    }


    @Test
    public void saveOrUpdate() throws IOException {
        LiteNote liteNote = LiteNoteHelper.getLiteNote("test", "test");
        liteNoteHardDiskService.saveOrUpdate(liteNote);
    }

    @Test
    public void search() throws IOException {
        List<LiteNote> liteNoteList = liteNoteHardDiskService.search("test", 10);
        liteNoteList.forEach(liteNote -> {
            Console.log("item==>{}", liteNote);
        });
    }


    @Test
    public void rebuildIndex() throws IOException {
        liteNoteHardDiskService.rebuildIndex();
    }
}