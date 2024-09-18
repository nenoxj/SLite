package cn.note.slite.core.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.List;

/**
 * 实现了索引文档更新和删除
 *
 * @author jee
 * @version 1.0
 */
public interface LuceneDocumentService extends LuceneOperationService {


    /**
     * 索引更新处理器
     * 该方法负责处理索引的更新操作。
     *
     * @throws IOException 如果索引更新过程中发生文件读写错误，则抛出该异常。
     */
    void indexUpdateHandler() throws IOException;


    default void deleteAll() throws IOException {
        getIndexWriter().deleteAll();
        indexUpdateHandler();
    }


    default void addDocument(Document document) throws IOException {
        getIndexWriter().addDocument(document);
        indexUpdateHandler();
    }


    default void addDocuments(List<Document> documents) throws IOException {
        getIndexWriter().addDocuments(documents);
        indexUpdateHandler();
    }

    default void updateDocuments(List<Document> documents, Term... terms) throws IOException {
        getIndexWriter().deleteDocuments(terms);
        getIndexWriter().addDocuments(documents);
        indexUpdateHandler();
    }


    /**
     * 更新指定文档中的信息
     *
     * @param document 需要更新的文档对象，包含要修改的信息
     * @param term     更新的条件，通常是一个包含文档唯一标识的键值对
     * @return 操作序列号
     * @throws IOException 如果文档存储操作遇到错误，则抛出此异常
     */
    default long updateDocument(Document document, Term term) throws IOException {
        long seqNo = getIndexWriter().updateDocument(term, document);
        indexUpdateHandler();
        return seqNo;
    }

    /**
     * 删除与给定术语关联的文档
     * 该方法主要用于在索引中删除特定文档，基于一个或多个术语
     *
     * @param terms 一个或多个{@link Term}对象，每个对象代表一个要删除的文档的唯一标识
     *              通常包含如文档ID等足以区分文档的字段
     * @return 操作序列号
     * @throws IOException 如果在删除过程中发生输入输出错误，可能会抛出此异常
     */
    default long deleteDocuments(Term... terms) throws IOException {
        long seqNo = getIndexWriter().deleteDocuments(terms);
        indexUpdateHandler();
        return seqNo;
    }

    /**
     * 根据文档ID更新指定文档
     *
     * @param id       文档的唯一标识符
     * @param document 需要更新的文档对象
     * @return 操作序列号
     * @throws IOException 如果在更新过程中发生I/O错误
     */
    default long updateDocumentById(String id, Document document) throws IOException {
        return updateDocument(document, new Term("id", id));
    }

    /**
     * 根据文档ID删除文档.
     *
     * @param id 文档的唯一标识符.
     * @return 操作序列号
     * @throws IOException 如果删除过程中发生任何输入或输出异常.
     */
    default long deleteDocumentById(String id) throws IOException {
        return deleteDocuments(new Term("id", id));
    }


    /**
     * 删除未使用的索引
     */
    default void deleteUnusedFiles() throws IOException {
        getIndexWriter().deleteUnusedFiles();
        indexUpdateHandler();
    }

}
