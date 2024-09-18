package cn.note.slite.core.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jee
 * @version 1.0
 */
public class AnalyzerUtil {

    public static List<String> getAnalyzerTokens(Analyzer analyzer, String str) throws IOException {
        try {
            //将字符串传话成流对象String流
            StringReader reader = new StringReader(str);
            //analyzer底层实现,是通过tokenStream来完成的,根据实现的不同实现类中的tokenStream方法,对数据流进行分词,属性计算.
            TokenStream tokenStream = analyzer.tokenStream("test", reader);
            tokenStream.reset();//重置属性,从头开始
            //从分词token流中获取词项属性,词项:分词的每个最小意义的词,就是一个词项
            CharTermAttribute attribute
                    = tokenStream.getAttribute(CharTermAttribute.class);
            List<String> resultList = new ArrayList<>(20);
            while (tokenStream.incrementToken()) {
                resultList.add(attribute.toString());
            }
            return resultList;
        } catch (IOException error) {
            throw error;
        } finally {
            analyzer.close();
        }
    }

}
