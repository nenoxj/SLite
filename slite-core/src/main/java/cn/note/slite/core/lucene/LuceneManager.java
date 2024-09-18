package cn.note.slite.core.lucene;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 索引管理器
 *
 * @author jee
 */
public class LuceneManager implements Closeable {
    private final static Logger logger = LoggerFactory.getLogger(LuceneManager.class);

    /**
     * 共享同一个线程池服务实例，实现异步任务的执行和管理
     */
    @Getter
    private ListeningExecutorService executorService;


    private static volatile LuceneManager INSTANCE;

    private static final int AWAIT_TIMEOUT = 2000;

    private LuceneManager() {
        // 获取当前系统可用的处理器核心数
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cpuCoreNum));


        // 注册关闭钩子，在JVM关闭时，关闭索引资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("IndexManager await index close ...,for {} millis", AWAIT_TIMEOUT);
            //延迟2秒关闭，防止有数据没提交进来
            try {
                Thread.sleep(AWAIT_TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Interrupted Exception", e);
            }
            try {
                close();
                logger.warn("IndexManager close index success !");
            } catch (IOException e) {
                logger.error("IndexManager close index error:", e);
            }
        }));
    }

    public static LuceneManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LuceneManager.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new LuceneManager();
                    } catch (Exception e) {
                        logger.error("Failed to create LuceneManager instance", e);
                        throw new IllegalStateException("Failed to initialize LuceneManager", e);                    }
                }
            }
        }
        return INSTANCE;
    }


    /* 绑定资源*/
    private final static ConcurrentHashMap<String, LuceneService<?>> LUCENE_SERVICE_MAP = new ConcurrentHashMap<>();


    public ListenableFuture<?> submit(Runnable runnable) {
        return executorService.submit(runnable);
    }

    /**
     * 监听索引服务
     *
     * @param luceneService 索引服务
     */
    public void listenerLuceneService(LuceneService<?> luceneService) {
        LUCENE_SERVICE_MAP.put(luceneService.getClass().getName(), luceneService);
    }


    @Override
    public void close() throws IOException {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.error("Pool did not terminate within timeout period");
            }
        } catch (InterruptedException e) {
            logger.error("executorService error", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LUCENE_SERVICE_MAP.forEach((k, v) -> {
            try {
                v.close();
            } catch (IOException e) {
                logger.error("索引关闭失败:", e);
            }
        });
        LUCENE_SERVICE_MAP.clear();
    }


    /**
     * 创建IndexWriter实例
     *
     * @return IndexWriter索引写入器实例
     * @throws IOException 创建IndexWriter过程中发生输入输出错误
     */
    public IndexWriter createIndexWriter(FSDirectory fsDirectory, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setMaxBufferedDocs(10000);
        indexWriterConfig.setRAMBufferSizeMB(500);
        return new IndexWriter(fsDirectory, indexWriterConfig);
    }


    /**
     * 创建IndexSearcher实例
     *
     * @return IndexSearcher 返回IndexSearcher实例，用于执行索引搜索操作
     * @throws IOException 创建IndexSearcher过程中发生输入输出错误
     */
    public IndexSearcher createIndexSearcher(IndexWriter indexWriter) throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter);
        return new IndexSearcher(directoryReader, executorService);
    }
}
