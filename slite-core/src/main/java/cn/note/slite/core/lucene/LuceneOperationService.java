package cn.note.slite.core.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author jee
 * @version 1.0
 */
public interface LuceneOperationService extends Closeable {

    Logger logger = LoggerFactory.getLogger(LuceneOperationService.class);

    IndexWriter getIndexWriter() throws IOException;

    IndexSearcher getIndexSearcher() throws IOException;

    Analyzer  getAnalyzer();

    @Override
    default void close() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.flush();
        indexWriter.commit();
        indexWriter.close();
    }
}
