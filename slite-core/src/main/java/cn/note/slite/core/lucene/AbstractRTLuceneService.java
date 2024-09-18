package cn.note.slite.core.lucene;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * 近实时更新服务
 *
 * @author jee
 * @version 1.0
 */
public abstract class AbstractRTLuceneService<T> implements LuceneService<T> {

    protected final LuceneManager luceneManager = LuceneManager.getInstance();

    protected IndexSearcher indexSearcher;

    protected IndexWriter indexWriter;

    private final Analyzer analyzer;

    private final FSDirectory fsDirectory;

    public AbstractRTLuceneService(FSDirectory fsDirectory) throws IOException {
        this(fsDirectory, new StandardAnalyzer());
    }

    public AbstractRTLuceneService(FSDirectory fsDirectory, Analyzer analyzer) throws IOException {
        this.analyzer = analyzer;
        this.fsDirectory = fsDirectory;
        this.indexWriter = luceneManager.createIndexWriter(fsDirectory, analyzer);
        this.indexSearcher = luceneManager.createIndexSearcher(indexWriter);
        luceneManager.listenerLuceneService(this);
    }

    @SuppressWarnings({"UnstableApiUsage", "SynchronizeOnNonFinalField"})
    @Override
    public void indexUpdateHandler() throws IOException {
        synchronized (this.indexSearcher) {
            try {
                //进实时转换
                this.indexSearcher = luceneManager.createIndexSearcher(getIndexWriter());
                logger.info("updateIndexSource ok");
            } catch (IOException e) {
                logger.error("Near Real-Time updateIndexSource error", e);
            } finally {

                ListenableFuture<Void> listenableFuture = luceneManager.getExecutorService().submit((Callable<Void>) () -> {
                    logger.info("source commit ...");
                    indexWriter.flush();
                    indexWriter.commit();
                    return null;
                });
                Futures.addCallback(listenableFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void aVoid) {
                        logger.info("source commit success");
                    }

                    @Override
                    public void onFailure(@Nonnull Throwable e) {
                        logger.error("{} - source commit error", fsDirectory.getDirectory(), e);
                    }
                }, luceneManager.getExecutorService());
            }
        }

    }


    @Override
    public IndexWriter getIndexWriter() throws IOException {
        return indexWriter;
    }

    @Override
    public IndexSearcher getIndexSearcher() throws IOException {
        return indexSearcher;
    }


    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
