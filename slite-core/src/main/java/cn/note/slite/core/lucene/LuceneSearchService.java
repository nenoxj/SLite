package cn.note.slite.core.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jee
 * @version 1.0
 */
public interface LuceneSearchService extends LuceneOperationService {

    /**
     * 统计数量
     */
    default int count(Query query) throws IOException {
        return getIndexSearcher().count(query);
    }


    default int count() throws IOException {
        return count(new MatchAllDocsQuery());
    }


    /**
     * 查询所有文档
     */
    default List<Document> queryAllDocs() throws IOException {
        return queryListDocs(new MatchAllDocsQuery());
    }


    default List<Document> queryListDocs(Query query) throws IOException {
        return queryListDocs(query, Integer.MAX_VALUE);
    }


    /**
     * 根据 Query 查询集合
     */
    default List<Document> queryListDocs(Query query, int num) throws IOException {
        ScoreDoc[] scoreDocs = search(query, num).scoreDocs;
        return getDocuments(scoreDocs);
    }


    /**
     * 根据搜索结果获取文档列表
     *
     * @param scoreDocs 搜索结果的分数文档数组，每个元素包含文档的索引和相关性分数
     * @return 返回一个包含Document对象的列表，代表搜索到的文档
     * @throws IOException 如果从索引中读取文档时发生异常，则抛出此异常
     *                     <p>
     *                     此方法通过ScoreDoc数组来获取相应的文档对象，主要用于在搜索结果中呈现详细的信息
     *                     它是默认方法，可以在接口中使用，提高了代码的可复用性和灵活性
     */
    default List<Document> getDocuments(ScoreDoc[] scoreDocs) throws IOException {
        List<Document> documents = new ArrayList<>(scoreDocs.length);
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = getDocument(scoreDoc.doc);
            documents.add(document);
        }
        return documents;
    }

    /**
     * 根据文档ID获取Document对象
     *
     * @param docID 文档的唯一标识符
     * @return 返回一个Document对象，代表了指定文档ID的文档
     * @throws IOException 如果在检索文档过程中发生输入输出错误
     */
    default Document getDocument(int docID) throws IOException {
        return getIndexSearcher().doc(docID);
    }

    /**
     * 默认方法，用于执行搜索操作
     * 该方法定义在搜索接口中，提供了一个基于查询对象来检索最相关文档的功能
     *
     * @param query 查询对象，表示用户输入的查询条件它封装了搜索的关键词和搜索选项
     *              通过这个参数，方法知道如何在索引中进行搜索
     * @param n     表示希望返回的搜索结果数量，即返回的TopDocs对象中应包含的最多文档数
     *              这个参数允许调用者控制结果集的大小，以便只获取最相关的文档
     * @return TopDocs对象，包含搜索结果，其中包含匹配查询的文档摘要及其相关分数
     * 返回值提供了关于找到的文档的信息，包括它们的相关性排序
     * @throws IOException 如果在搜索过程中发生输入输出错误，可能会抛出此异常
     *                     由于搜索操作涉及到对索引的读取，因此在文件操作或索引结构解析时可能发生异常
     */
    default TopDocs search(Query query, int n) throws IOException {
        return getIndexSearcher().search(query, n);
    }
}
