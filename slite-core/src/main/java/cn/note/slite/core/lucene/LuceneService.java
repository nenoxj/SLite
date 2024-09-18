package cn.note.slite.core.lucene;

import cn.hutool.core.lang.Console;
import cn.note.slite.core.entity.Page;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jee
 * @version 1.0
 */
public interface LuceneService<T> extends LuceneSearchService, LuceneDocumentService {

    /**
     * 将 Lucene文档转换为指定类型的 Java 对象
     *
     * @param document MongoDB 文档，表示需要转换的数据源
     * @return T        返回转换后的指定类型的 Java 对象
     */
    T toBean(Document document);

    /**
     * 将给定对象转换为Document
     * 此方法用于将任何类型的对象T转换为Document类型，以便在需要Document表示的对象时进行操作
     *
     * @param t 要转换为Document的对象，类型为泛型T
     * @return 返回转换后的Document对象
     */
    Document toDocument(T t);

    default long saveOrUpdateById(String id, T t) throws IOException {
        return updateDocumentById(id, toDocument(t));
    }

    default List<T> queryList(Query query) throws IOException {
        return queryList(query, Integer.MAX_VALUE);
    }

    default List<T> queryList(Query query, int size) throws IOException {
        List<Document> documentList = queryListDocs(query, size);
        List<T> resultList = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            resultList.add(toBean(document));
        }
        return resultList;
    }


    default List<T> queryAll() throws IOException {
        List<Document> documentList = queryAllDocs();
        List<T> resultList = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            resultList.add(toBean(document));
        }
        return resultList;
    }


    default Page<T> queryPageList(Query query, int currentPage, int pageSize) throws IOException {
        TopDocs topDocs = null;
        if (currentPage == 1) {
            topDocs = getIndexSearcher().search(query, pageSize);
        } else {
            int start = (currentPage - 1) * pageSize;
            IndexSearcher searcher = getIndexSearcher();
            topDocs = searcher.search(query, start);
            ScoreDoc preScore = topDocs.scoreDocs[start - 1];
            topDocs = searcher.searchAfter(preScore, query, pageSize);
        }
        long totalCount = topDocs.totalHits.value;
        List<Document> documentList = getDocuments(topDocs.scoreDocs);
        List<T> resultList = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            resultList.add(toBean(document));
        }
        return new Page<>(resultList, totalCount, currentPage, pageSize);
    }


}
