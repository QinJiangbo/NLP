package com.qinjiangbo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ServiceTestMain {
	
	public static void main(String[] args) throws Exception {
		
		String sentence = "I wish you do it for your own";
		System.out.println("score===>"+SentenceChecker.scoreSentence(sentence));
		Map<List<String>, Float> scores = SentenceChecker.ngramsCheck(sentence);
		Set<Entry<List<String>, Float>> entrySet = scores.entrySet();
		for(Entry<List<String>, Float> entry : entrySet) {
			System.out.println(entry.getKey() + "===>" + entry.getValue());
		}
		
//		@SuppressWarnings("unused")
//		WeightConfig weightConfig = new WeightConfig();
//		ParamsConfig paramsConfig = new ParamsConfig();
//		
//		String filePath = "C:/Users/Richard/Desktop/data/3.2.txt";
//		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
//		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//		BufferedReader br = new BufferedReader(inputStreamReader);
//		String text1 = null;
//		StringBuilder sb = new StringBuilder();
//		while((text1 = br.readLine()) != null) {
//			sb.append(text1);
//		}
//		br.close();
//		inputStreamReader.close();
//		fileInputStream.close();
//		
//		String text = sb.toString();
//		System.out.println(text);
//		
//		PostProcessor postProcessor = new PostProcessor();
//		PreProcessor preProcessor = new PreProcessor();
//		String[] keyWords = {"changes", "computer", "countries", "family"};
//		postProcessor.setKeyWords(keyWords);
//		Type type = Type.LARGETEXT;
//		postProcessor.setType(type);
//		paramsConfig.setMinWordNum(280);
//		preProcessor.process(text);
//		@SuppressWarnings("unused")
//		List<String> Sentences = preProcessor.getSentences();
//		List<String> words = preProcessor.getWords();
//		for(String word : words) {
//			System.out.println(word);
//		}
//		System.out.println("========================>>>>>>>>>>>>>>>>>>>>>>>>>");
//		Map<String, Object> map = postProcessor.processConventions(Sentences, words, weightConfig, paramsConfig);
//		@SuppressWarnings("unchecked")
//		List<String> mispelledWords = (List<String>) map.get("mispelledWords");
//		for(String mispell: mispelledWords) {
//			System.out.println(mispell);
//		}
		
//		Map<String, Object> scores = postProcessor.process(text, weightConfiguration, paramsConfiguration);
//		Set<String> keys = scores.keySet();
//		for(String key : keys) {
//			System.out.println(key+" : "+scores.get(key));
//		}
		
//		Map<String, Integer> cache = CorpusCache.loadCache();
//		System.out.println(cache.size());
		
	}
}
