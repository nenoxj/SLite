package cn.note.slite.litenote.service;

import cn.hutool.core.util.StrUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;
import cn.note.slite.core.lucene.AbstractRTLuceneService;
import cn.note.slite.core.lucene.LuceneQueryBuilder;
import cn.note.slite.core.lucene.analyzer.AnalyzerUtil;
import cn.note.swing.core.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LiteNoteLuceneService extends AbstractRTLuceneService<LiteNote> {

    public LiteNoteLuceneService(FSDirectory fsDirectory) throws IOException {
        super(fsDirectory);
    }

    @Override
    public LiteNote toBean(Document document) {
        Map<String, String> fields = new HashMap<>();
        document.forEach(doc -> {
            fields.put(doc.name(), doc.stringValue());
        });

        return ConvertUtil.convertAsBean(fields, LiteNote.class);
    }

    @Override
    public Document toDocument(LiteNote liteNote) {
        Document document = new Document();
        document.add(new StringField("id", liteNote.getId(), Field.Store.YES));
        document.add(new TextField("content", liteNote.getContent(), Field.Store.YES));
        if (StrUtil.isNotBlank(liteNote.getGroups())) {
            document.add(new TextField("groups", liteNote.getGroups(), Field.Store.YES));
        }
        return document;
    }


    public void batchSave(List<LiteNote> liteNoteList) throws IOException {
        List<Document> documents = liteNoteList.stream().map(this::toDocument).collect(Collectors.toList());
        super.addDocuments(documents);
    }

    public void saveOrUpdate(LiteNote liteNote) throws IOException {
        updateDocumentById(liteNote.getId(), toDocument(liteNote));
    }

    public void deleteById(String id) throws IOException {
        super.deleteDocumentById(id);
    }

    /**
     * 根据搜索文本搜索相关的 LiteNote。
     * <p>
     * 此方法主要用于在给定的搜索文本基础上，查找并返回一系列满足条件的 LiteNote。
     * 它通过考虑搜索文本的内容，检索与之相关联的 LiteNote 实例，并限制返回结果的大小。
     *
     * @param searchText 搜索文本，用于匹配 LiteNote 的内容。
     * @param size       返回结果的最大数量，用于控制返回的 LiteNote 数量。
     * @return 包含匹配 LiteNote 的列表，如果找不到任何匹配项，则返回空列表。
     * @throws IOException 如果在执行搜索过程中发生输入输出错误。
     */
    public List<LiteNote> search(String searchText, int size) throws IOException {
        if (StrUtil.isBlank(searchText)) {
            return Collections.emptyList(); // 避免返回null
        }
        return super.queryList(convertQuery(searchText), size);
    }


    /**
     * 分页搜索
     */
    public Page<LiteNote> searchPage(String searchText, int currentPage, int pageSize) throws IOException {
        if (StrUtil.isBlank(searchText)) {
            return new Page<>(Collections.emptyList(), 0L, currentPage, pageSize);
        }
        return super.queryPageList(convertQuery(searchText), currentPage, pageSize);
    }


    private Query convertQuery(String searchText) throws IOException {
        String searchGroup = null;
        String searchContent = searchText;

        // 处理特殊情况，如只包含冒号的情况
        if (searchText.contains(":")) {
            String[] searchGroups = searchText.split(":", 2); // 使用limit参数防止分割次数过多
            if (searchGroups.length == 2) {
                searchGroup = searchGroups[0];
                searchContent = searchGroups[1];
            }
        }
        LuceneQueryBuilder queryBuilder = new LuceneQueryBuilder();
        if (StrUtil.isNotBlank(searchGroup)) {
            queryBuilder.fuzzyQuery(new FuzzyQuery(new Term("groups", searchGroup)), BooleanClause.Occur.MUST);
        }

        if (StrUtil.isNotBlank(searchContent)) {
            List<String> queryTokens = AnalyzerUtil.getAnalyzerTokens(new StandardAnalyzer(), searchContent);
            queryTokens.forEach(token -> queryBuilder.wildcardQuery(new WildcardQuery(new Term("content", '*' + token + '*')), BooleanClause.Occur.MUST));
        }

        Query query = queryBuilder.build();
        log.debug("Full query expression: \n groups={} , content={}", query.toString("groups"), query.toString("content"));
        return query;
    }

}
