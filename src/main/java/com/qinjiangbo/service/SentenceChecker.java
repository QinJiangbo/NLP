package com.qinjiangbo.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.qinjiangbo.util.CorpusCache;
import com.qinjiangbo.util.FilePath;
import com.qinjiangbo.util.LanguageModel;
import com.qinjiangbo.util.ParamsConfig;
import org.springframework.stereotype.Service;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.io.LmReaders;

@Service
public class SentenceChecker {
	
	/**
	 * 文章所有句子语法检查
	 * @param Sentences
	 * @return
	 */
	public static Map<String, Float> grammarCheck(List<String> Sentences) {
		Map<String, Float> scores = new HashMap<String, Float>();
		final ConfigOptions configOptions = new ConfigOptions();
		final ArrayEncodedProbBackoffLm<String> lm = 
				LmReaders.readArrayEncodedLmFromArpa(FilePath.LMFILE,
						true, new StringWordIndexer(), configOptions, Integer.MAX_VALUE);
		for(String Sentence: Sentences) {
			String[] terms = Sentence.split(" ");
			float score = LanguageModel.scoreSentence(Arrays.asList(terms), lm);
			score = score / terms.length;
			scores.put(Sentence, new Float(score));
		}
		return scores;
	}
	
	/**
	 * 单句子语法检查
	 * @param sentence
	 * @return
	 */
	public static Float scoreSentence(String sentence) {
		final ConfigOptions configOptions = new ConfigOptions();
		final ArrayEncodedProbBackoffLm<String> lm = 
				LmReaders.readArrayEncodedLmFromArpa(FilePath.LMFILE, 
						true, new StringWordIndexer(), configOptions, Integer.MAX_VALUE);
		String[] terms = sentence.split(" ");
		Float score = LanguageModel.scoreSentence(Arrays.asList(terms), lm);
		score = score / terms.length;
		return score;
	}
	
	/**
	 * 单句子语法检查
	 * @param sentence
	 * @return
	 */
	public static Map<List<String>, Float> ngramsCheck(String sentence) {
		final ConfigOptions configOptions = new ConfigOptions();
		final ArrayEncodedProbBackoffLm<String> lm = 
				LmReaders.readArrayEncodedLmFromArpa(FilePath.LMFILE, 
						true, new StringWordIndexer(), configOptions, Integer.MAX_VALUE);
		String[] terms = sentence.split(" ");
		Map<List<String>, Float> scores = LanguageModel.scoreNgrams(Arrays.asList(terms), lm);
		return scores;
	}
	
	/**
	 * 主题相关检测
	 * @param Sentences 被检测句子
	 * @return 主题相关单词数量
	 */
	public static int topicCheck(List<String> Sentences) {
		countOcurrences("", "");
		return 0;
	}
	
	/**
	 * 流畅度检测: 目前算法是将句子与语料库中单词数同样超过7个的句子作对比,<br>
	 * 		           将重合度超过70%的句子认定为流畅句子.<br>
	 * @param Sentences
	 * @return
	 */
	public static int fluencyCheck(List<String> Sentences, ParamsConfig paramsConfig) {
		int count = 0;
		Map<String, Integer> corpusMap = CorpusCache.loadCache();
		Set<Entry<String, Integer>> entrySet = corpusMap.entrySet();
		for(String sentence : Sentences) {
			for(Entry<String, Integer> entry : entrySet) {
				if(entry.getValue() >= paramsConfig.getSentenceWordNum()) {
					String[] str1 = sentence.split(",| |;");
					String[] str2 = entry.getKey().split(",| |;");
					int maxComm = maxCommLength(str1, str2);
					if(maxComm >= (int)(str2.length * paramsConfig.getSentenceSimilarity())) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}
	
	/**
	 * 计算整篇文章的berkely句子分值平均值, 目前有个最高分值和最低分值, 分别对应的是100-0分.<br>
	 * 分数整体是取对数生成的, 所以是负值, 值越大分数越高(eg. -5(high) > -100(low))<br>
	 * @param Sentences
	 * @return
	 */
	public static int avgBerkelyScore(List<String> Sentences) {
		int totalScore = 0;
		final ConfigOptions configOptions = new ConfigOptions();
		final ArrayEncodedProbBackoffLm<String> arrayEncodedProbBackoffLm = 
				LmReaders.readArrayEncodedLmFromArpa(FilePath.LMFILE, 
						true, new StringWordIndexer(), configOptions, Integer.MAX_VALUE);
		for(String Sentence: Sentences) {
			String[] terms = Sentence.split(" ");
			float score = arrayEncodedProbBackoffLm.scoreSentence(Arrays.asList(terms));
			score = score / terms.length;
			totalScore += score;
		}
		int avgBerkelyScore = (int)(totalScore / Sentences.size());
		return avgBerkelyScore;
	}
	
	/**
	 * 计算subStr在str中出现的次数<br>
	 * 这个方法与{@link WordsChecker}共用
	 * @param str 需要被比较的字符串
	 * @param subStr 子字符串
	 * @return 子字符串在被比较的字符串中出现的次数
	 */
	private static int countOcurrences(String str, String subStr) {
		subStr = subStr.trim();
		int count = 0;
		int index = 0;
		while((index = str.indexOf(subStr)) != -1) {
			str = str.substring(index+subStr.length());
			count++;
		}
		return count;
	}
	
	/**
	 * 取两个字符串数组的最大公共字符串数量
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int maxCommLength(String[] str1, String[] str2) {
		int len1 = str1.length, len2 = str2.length;
		int maxLen = 0; //最大长度
		for(int i = 0; i < len1; i++) {
			for(int j = 0; j < len2; j++) {
				if(str1[i].equals(str2[j])) {
					int k = 1;
					while(true) {
 						if( (i+k) < len1 && (j+k) < len2 && str1[i+k].equals(str2[j+k])) {
							k++;
						}else{
							break;
						}
					}
					if(k > maxLen) {
						maxLen = k;
					}
				}
			}
		}
		return maxLen;
	}
}
