package cn.note.slite.litenote.service;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.lucene.LuceneQueryBuilder;
import cn.note.slite.core.lucene.analyzer.AnalyzerUtil;
import cn.note.slite.core.util.CallbackHelper;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/**
 * 查询测试
 */
public class LiteNoteLuceneServiceQueryTest {

    private LiteNoteLuceneService liteNoteLuceneService;


    @Before
    public void before() throws IOException {
        Path path = Paths.get("D:\\lucene-test", "notes-query");
        FSDirectory fsDirectory = FSDirectory.open(path);
        liteNoteLuceneService = new LiteNoteLuceneService(fsDirectory);
    }


    @Test
    public void rebuild() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            liteNoteLuceneService.deleteAll();
            LiteNoteHelper.createLiteNote("netstat -antlp | grep 9876 ", "linux");
            LiteNoteHelper.createLiteNote("find / -name '*.log' | xargs rm -rf", "linux");
            LiteNoteHelper.createLiteNote("find . -type f -size +300M", "linux");
            LiteNoteHelper.createLiteNote("find / -type f -size +50M -name *.log | xagrgs rm -rf", "");
            LiteNoteHelper.createLiteNote("md5sum mysql5.7.tar.gz ", "md5");
            LiteNoteHelper.createLiteNote("scp -r root@192.168.210.140:/data/plugins /data/", "linux");
            LiteNoteHelper.createLiteNote("scp -r ./target root@192.168.210.140:/data/ ", "linux");
            LiteNoteHelper.createLiteNote("chmod -R 755 /home/test", "chmod");
            liteNoteLuceneService.batchSave(LiteNoteHelper.liteNoteList);
            Console.log("indices size==>{}", liteNoteLuceneService.count());
        });

    }


    @Test
    public void queryByContent() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            LuceneQueryBuilder queryBuilder = new LuceneQueryBuilder();
            List<String> queryTokens = AnalyzerUtil.getAnalyzerTokens(liteNoteLuceneService.getAnalyzer(), "find arg");
            Console.log("query tokens====>{}", JSONUtil.toJsonStr(queryTokens));
            queryTokens.forEach(token -> queryBuilder.wildcardQuery(new WildcardQuery(new Term("content", '*' + token + '*')), BooleanClause.Occur.MUST));
            List<LiteNote> liteNotes = liteNoteLuceneService.queryList(queryBuilder.build());
            Console.log("search total==>{}", liteNotes.size());
            liteNotes.forEach(liteNote -> Console.log("search item==>{}", liteNote));
        });
    }


    @Test
    public void queryByGroup() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            LuceneQueryBuilder queryBuilder = new LuceneQueryBuilder();
            queryBuilder.fuzzyQuery(new FuzzyQuery(new Term("groups", "linx")), BooleanClause.Occur.MUST);
            List<LiteNote> liteNotes = liteNoteLuceneService.queryList(queryBuilder.build());
            Console.log("search total==>{}", liteNotes.size());
            liteNotes.forEach(liteNote -> Console.log("search item==>{}", liteNote));
        });
    }


    @Test
    public void queryByMultiField() throws Exception {
        CallbackHelper.stopwatchProxy(() -> {
            LuceneQueryBuilder queryBuilder = new LuceneQueryBuilder();
            String searchText = "linux:find";
            String searchGroup = null;
            String searchContent = null;
            if (searchText.contains(":")) {
                String[] searchGroups = searchText.split(":");
                searchGroup = searchGroups[0];
                searchContent = searchGroups[1];
            } else {
                searchContent = searchText;
            }

            if (StrUtil.isNotBlank(searchGroup)) {
                queryBuilder.fuzzyQuery(new FuzzyQuery(new Term("groups", searchGroup)), BooleanClause.Occur.MUST);
            }
            List<String> queryTokens = AnalyzerUtil.getAnalyzerTokens(liteNoteLuceneService.getAnalyzer(), searchContent);
            queryTokens.forEach(token -> queryBuilder.wildcardQuery(new WildcardQuery(new Term("content", '*' + token + '*')), BooleanClause.Occur.MUST));
            Query query = queryBuilder.build();
            Console.log("query expression:{}  ,{}", query.toString("groups"), query.toString("content"));
            List<LiteNote> liteNotes = liteNoteLuceneService.queryList(query);
            Console.log("search total==>{}", liteNotes.size());
            liteNotes.forEach(liteNote -> Console.log("search item==>{}", liteNote));
        });
    }
}