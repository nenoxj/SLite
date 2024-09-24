package cn.note.slite.litenote.service;

import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;
import cn.note.swing.core.filestore.RelativeFileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 硬盘存储
 * 更新笔记及创建索引
 */
@Slf4j
public class LiteNoteHardDiskService implements LiteNoteService {

    private RelativeFileStore fileStore;

    private LiteNoteLuceneService liteNoteLuceneService;

    private LiteNoteJsonService liteNoteJsonService;

    private LiteNoteMdService liteNoteMdService;

    public LiteNoteHardDiskService(File dataDir, File indexDir) throws IOException {
        FileUtils.forceMkdir(dataDir);
        if (!dataDir.isDirectory()) {
            throw new IOException(dataDir + " is not directory!");
        }

        fileStore = new RelativeFileStore(dataDir);
        liteNoteLuceneService = new LiteNoteLuceneService(FSDirectory.open(indexDir.toPath()));
        liteNoteJsonService = new LiteNoteJsonService(fileStore);
        liteNoteMdService = new LiteNoteMdService(fileStore);
    }


    @Override
    public void batchSave(List<LiteNote> liteNoteList) throws IOException {
        liteNoteJsonService.batchSave(liteNoteList);
        liteNoteLuceneService.batchSave(liteNoteList);
    }

    @Override
    public void saveOrUpdate(LiteNote liteNote) throws IOException {
        liteNoteJsonService.saveOrUpdate(liteNote);
        liteNoteLuceneService.saveOrUpdate(liteNote);
    }

    @Override
    public void deleteById(String id) throws IOException {
        liteNoteJsonService.deleteById(id);
        liteNoteLuceneService.deleteById(id);
    }


    @Override
    public List<LiteNote> search(String text, int size) throws IOException {
        return liteNoteLuceneService.search(text, size);
    }

    @Override
    public Page<LiteNote> searchPage(String text, int currentPage, int pageSize) throws IOException {
        return liteNoteLuceneService.searchPage(text, currentPage, pageSize);
    }

    @Override
    public void rebuildIndex() throws IOException {
        List<LiteNote> liteNoteJsonList = liteNoteJsonService.listAll();
        List<LiteNote> resultList = new ArrayList<>(liteNoteJsonList);
        List<LiteNote> liteNoteMdList = liteNoteMdService.listAll();
        resultList.addAll(liteNoteMdList);
        log.info("create index, json size==>{} ,md size==>{}", liteNoteJsonList.size(), liteNoteMdList.size());
        liteNoteLuceneService.deleteAll();
        liteNoteLuceneService.batchSave(resultList);
    }

    @Override
    public void changeStoreDirectory(File storeDirectory) throws IOException {
        if (!storeDirectory.exists()) {
            throw new IOException(storeDirectory + " not exists!");
        }

        if (!storeDirectory.isDirectory()) {
            throw new IOException(storeDirectory + " is not directory !");
        }
        fileStore.setHomeDir(storeDirectory);
        rebuildIndex();
    }
}
