package cn.note.slite.core.lucene;

import cn.hutool.core.lang.Console;
import cn.note.slite.core.lucene.analyzer.AnalyzerUtil;
import cn.note.slite.core.lucene.analyzer.CharacterAnalyzer;
import cn.note.slite.core.lucene.analyzer.PatternAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

/**
 * @author jee
 * @version 1.0
 */
public class AnalyzerTest {

    private static void printAnalyzerToken(Analyzer analyzer, String str) throws Exception {
        AnalyzerUtil.getAnalyzerTokens(analyzer, str).forEach(token -> {
            Console.log("token==>{}", token);
        });

    }

    String text = "find / -type f -size +50M -name *.log | xagrgs rm -rf";

    @Test
    public void StandardAnalyzer() throws Exception {
        printAnalyzerToken(new StandardAnalyzer(), text);
    }


    @Test
    public void SimpleAnalyzer() throws Exception {
        printAnalyzerToken(new SimpleAnalyzer(), text);
    }

    @Test
    public void WhitespaceAnalyzer() throws Exception {
        printAnalyzerToken(new WhitespaceAnalyzer(), text);
    }

    @Test
    public void CharacterAnalyzer() throws Exception {
        printAnalyzerToken(new CharacterAnalyzer(), text);
    }

    @Test
    public void PatternAnalyzer() throws Exception {
        printAnalyzerToken(new PatternAnalyzer("\\s+"), text);
    }
}
