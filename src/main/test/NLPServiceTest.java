import com.qinjiangbo.service.ScoreProcessor;
import com.qinjiangbo.service.SentenceChecker;
import com.qinjiangbo.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class NLPServiceTest {

    @Autowired
    private ScoreProcessor postProcessor;

    @Test
    public void testMain() throws Exception {

        WeightConfig weightConfig = new WeightConfig();
        ParamsConfig paramsConfig = new ParamsConfig();

        String filePath = FilePath.rootPath + "3.2.txt";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String text1 = null;
        StringBuilder sb = new StringBuilder();
        while ((text1 = br.readLine()) != null) {
            sb.append(text1 + "\n");
        }
        br.close();
        inputStreamReader.close();
        fileInputStream.close();

        String text = sb.toString();
        System.out.println(text);

        String[] keyWords = {"changes", "computer", "countries", "family"};
        postProcessor.setKeyWords(keyWords);
        TextType type = TextType.LARGETEXT;
        postProcessor.setTextType(type);
        paramsConfig.setMinWordNum(280);

        Map<String, Object> scores = postProcessor.process(text, weightConfig, paramsConfig);
        Set<String> keys = scores.keySet();
        for (String key : keys) {
            System.out.println(key + " : " + scores.get(key));
        }

        Map<String, Integer> cache = CorpusCache.loadCache();
        System.out.println(cache.size());

        String sentence = "There is many students";
        System.out.println("score===>" + SentenceChecker.scoreSentence(sentence));
        Map<List<String>, Float> scores1 = SentenceChecker.ngramsCheck(sentence);
        Set<Entry<List<String>, Float>> entrySet = scores1.entrySet();
        for (Entry<List<String>, Float> entry : entrySet) {
            System.out.println(entry.getKey() + "===>" + entry.getValue());
        }

    }
}
