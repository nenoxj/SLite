package cn.note.slite.core.lucene.analyzer;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.pattern.PatternTokenizer;

import java.util.regex.Pattern;

/**
 * 正则分词器
 *
 * @author jee
 * @version 1.0
 */
public class PatternAnalyzer extends Analyzer {
    //使用正则拆分式
    String regex;

    public PatternAnalyzer(String regex) {
        this.regex = regex;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return new TokenStreamComponents(new PatternTokenizer(Pattern.compile(regex), -1));
    }
}