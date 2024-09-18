package cn.note.slite.core.lucene.analyzer;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 字符分词器
 *
 * @author jee
 * @version 1.0
 */
public class CharacterAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new CharacterTokenizer();
        return new TokenStreamComponents(tokenizer);
    }
}