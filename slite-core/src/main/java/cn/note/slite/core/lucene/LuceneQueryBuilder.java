package cn.note.slite.core.lucene;

import org.apache.lucene.search.*;

/**
 * 查询转换器
 */
public class LuceneQueryBuilder {

    BooleanQuery.Builder builder;

    public LuceneQueryBuilder() {
        builder = new BooleanQuery.Builder();
    }

    public Query build() {
        return builder.build();
    }

    /**
     * 增加一个 query
     */
    public LuceneQueryBuilder addQuery(Query query, BooleanClause.Occur qccur) {
        builder.add(query, qccur);
        return this;
    }

    /**
     * 术语查询
     */
    public LuceneQueryBuilder termQuery(TermQuery termQuery, BooleanClause.Occur qccur) {
        builder.add(termQuery, qccur);
        return this;
    }

    /**
     * 通配符查询
     */
    public LuceneQueryBuilder wildcardQuery(WildcardQuery wildcardQuery, BooleanClause.Occur qccur) {
        builder.add(wildcardQuery, qccur);
        return this;
    }

    /**
     * phraseQuery
     */
    public LuceneQueryBuilder phraseQuery(PhraseQuery phraseQuery, BooleanClause.Occur qccur) {
        builder.add(phraseQuery, qccur);
        return this;
    }

    /**
     * 前缀查询
     */
    public LuceneQueryBuilder prefixQuery(PrefixQuery prefixQuery, BooleanClause.Occur qccur) {
        builder.add(prefixQuery, qccur);
        return this;
    }

    /**
     * 多短语查询
     */
    public LuceneQueryBuilder multiPhraseQuery(MultiPhraseQuery multiPhraseQuery, BooleanClause.Occur qccur) {
        builder.add(multiPhraseQuery, qccur);
        return this;
    }

    /**
     * 模糊查询
     */
    public LuceneQueryBuilder fuzzyQuery(FuzzyQuery fuzzyQuery, BooleanClause.Occur qccur) {
        builder.add(fuzzyQuery, qccur);
        return this;
    }

    /**
     * regexpQuery
     */
    public LuceneQueryBuilder regexpQuery(RegexpQuery regexpQuery, BooleanClause.Occur qccur) {
        builder.add(regexpQuery, qccur);
        return this;
    }

    /**
     * 术语范围查询
     */
    public LuceneQueryBuilder termRangeQuery(TermRangeQuery termRangeQuery, BooleanClause.Occur qccur) {
        builder.add(termRangeQuery, qccur);
        return this;
    }

    /**
     * 点范围查询
     */
    public LuceneQueryBuilder pointRangeQuery(PointRangeQuery pointRangeQuery, BooleanClause.Occur qccur) {
        builder.add(pointRangeQuery, qccur);
        return this;
    }

    /**
     * 常量分数查询
     */
    public LuceneQueryBuilder constantScoreQuery(ConstantScoreQuery constantScoreQuery, BooleanClause.Occur qccur) {
        builder.add(constantScoreQuery, qccur);
        return this;
    }

    /**
     * 分离Max Query
     */
    public LuceneQueryBuilder disjunctionMaxQuery(DisjunctionMaxQuery disjunctionMaxQuery, BooleanClause.Occur qccur) {
        builder.add(disjunctionMaxQuery, qccur);
        return this;
    }

    /**
     * 匹配所有文档查询
     */
    public LuceneQueryBuilder matchAllDocsQuery(MatchAllDocsQuery matchAllDocsQuery, BooleanClause.Occur qccur) {
        builder.add(matchAllDocsQuery, qccur);
        return this;
    }

    public LuceneQueryBuilder mergeQuery(LuceneQueryBuilder... queryBuilders) {
        for (LuceneQueryBuilder queryBuilder : queryBuilders) {
            if (queryBuilder != null) {
                builder.add(queryBuilder.build(), BooleanClause.Occur.MUST);
            }
        }
        return this;
    }
}
