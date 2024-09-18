package cn.note.slite.litenote.service;

import cn.hutool.json.JSONUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;
import cn.note.swing.core.filestore.RelativeFileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
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

    private static final String SUFFIX_LITE_NOTE = ".slite.json";

    private RelativeFileStore fileStore;

    private LiteNoteLuceneService liteNoteLuceneService;

    public LiteNoteHardDiskService(File dataDir, File indexDir) throws IOException {
        FileUtils.forceMkdir(dataDir);
        if (!dataDir.isDirectory()) {
            throw new IOException(dataDir + " is not directory!");
        }

        fileStore = new RelativeFileStore(dataDir);
        liteNoteLuceneService = new LiteNoteLuceneService(FSDirectory.open(indexDir.toPath()));
    }


    @Override
    public void batchSave(List<LiteNote> liteNoteList) throws IOException {
        for (LiteNote liteNote : liteNoteList) {
            String id = liteNote.getId();
            String fileName = id.concat(SUFFIX_LITE_NOTE);
            fileStore.writeRelativeFile(fileName, JSONUtil.toJsonStr(liteNote));
        }
        liteNoteLuceneService.batchSave(liteNoteList);
    }

    @Override
    public void saveOrUpdate(LiteNote liteNote) throws IOException {
        // 写入本地
        String id = liteNote.getId();
        String fileName = id.concat(SUFFIX_LITE_NOTE);
        fileStore.writeRelativeFile(fileName, JSONUtil.toJsonStr(liteNote));
        // 更新索引
        liteNoteLuceneService.saveOrUpdate(liteNote);
    }

    @Override
    public void deleteById(String id) throws IOException {
        String fileName = id.concat(SUFFIX_LITE_NOTE);
        fileStore.deleteRelativeFile(fileName);
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
        List<LiteNote> liteNoteList = new ArrayList<>(100);
        fileStore.lists(FileFilterUtils.suffixFileFilter(SUFFIX_LITE_NOTE), null, path -> {
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
        liteNoteLuceneService.deleteAll();
        liteNoteLuceneService.batchSave(liteNoteList);
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
