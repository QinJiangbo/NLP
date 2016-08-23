package com.qinjiangbo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.qinjiangbo.util.*;
import com.qinjiangbo.vojo.Term;
import org.springframework.stereotype.Service;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ObjectToCounterMap;

import static com.qinjiangbo.util.Hunspell.*;

@Service
public class WordsChecker {
	
	private static Hunspell.Dictionary enUS_Dict = null;
	private static Hunspell.Dictionary enGB_Dict = null; 
	private static List<String> logics = new ArrayList<String>();
	
	/**
	 * 单词拼写检查
	 * @param words 单词集合
	 * @return 错误单词集合
	 */
	public static List<String> spellCheck(List<String> words) {
		List<String> mispelledWords = new ArrayList<String>();
		try {
			if(enUS_Dict == null && enGB_Dict == null) {
				String dictDir = FilePath.DICTDIR;
				String libDir = FilePath.LIBDIR;
				String US_Lang = "en-US"; //使用美式英语
				String GB_Lang = "en-GB"; //增加英式英语
				enUS_Dict = getInstance(libDir).getDictionary(dictDir + "/" + US_Lang);
				enGB_Dict = getInstance(libDir).getDictionary(dictDir + "/" + GB_Lang);
			}
			for(int i=0; i < words.size(); i++) {
				String word = words.get(i);
				if(enUS_Dict.misspelled(word) && enGB_Dict.misspelled(word)) {
					if(!mispelledWords.contains(word)) {
						mispelledWords.add(word);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mispelledWords;
	}
	
	/**
	 * 统计超过或者少于长度为N的单词数量
	 * @param mode 模式 LengthMode.MORETHAN和Mode.LESSTHAN
	 * @param length 单词长度标准
	 * @param words 原文单词
	 * @return 单词数量
	 */
	public static int countWordsWithMode(LengthMode mode, int length, List<String> words) {
		int num = 0;
		//需要排除拼写错误的单词
		List<String> mispelledWords = spellCheck(words);
		if (mode == LengthMode.MORETHAN) {
			for(String word: words) {
				if(!mispelledWords.contains(word) && word.length() > length) {
					num++;
				}
			}
		}
		else if(mode == LengthMode.LESSTHAN) {
			for(String word: words) {
				if(!mispelledWords.contains(word) && word.length() < length) {
					num++;
				}
			}
		}
		return num;
	}
	
	/**
	 * 统计超过或者少于长度为N的单词数量
	 * @param mode 模式 LengthMode.MORETHAN和Mode.LESSTHAN
	 * @param words 原文单词
	 * @param paramsConfiguration
	 * @return 单词数量
	 */
	public static int countWordsWithMode(LengthMode mode, List<String> words, ParamsConfig paramsConfiguration) {
		int num = 0;
		int length = 0;
		//需要排除拼写错误的单词
		List<String> mispelledWords = spellCheck(words);
		if (mode == LengthMode.MORETHAN) {
			length = paramsConfiguration.getOverLengthN();
			for(String word: words) {
				if(!mispelledWords.contains(word) && word.length() > length) {
					num++;
				}
			}
		}
		else if(mode == LengthMode.LESSTHAN) {
			length = paramsConfiguration.getBelowLengthN();
			for(String word: words) {
				if(!mispelledWords.contains(word) && word.length() < length) {
					num++;
				}
			}
		}
		return num;
	}
	
	/**
	 * 全文单词重复率检测
	 * @param words 全文单词
	 * @param paramsConfiguration
	 * @return 单词重复数量
	 */
	public static int repeatCheck(List<String> words, ParamsConfig paramsConfiguration) {
		int num = 0;
		List<Term> terms = new ArrayList<Term>();
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);
		//所有的单词预处理
		for(String word : words) {
			tfIdf.handle(word);
		}
		//计算所有的单词出现频率
		for(String word: tfIdf.termSet()) {
			Term term = new Term();
			term.setWord(word);
			term.setDocFreq(tfIdf.docFrequency(word));
			terms.add(term);
		}
		Collections.sort(terms, new Comparator<Term>() {
			@Override
			public int compare(Term o1, Term o2) {
				return o2.getDocFreq() - o1.getDocFreq();
			}
		});
		int N = paramsConfiguration.getTopNwords();
		N = N > terms.size() ? terms.size() / 2 : N;
		for(int i=0; i<N; i++) {
			num = num + terms.get(i).getDocFreq();
		}
		return num;
	}
	
	/**
	 * 提取出关键词
	 * @param queContent 题目题干
	 * @return 题干关键词
	 */
	public static List<String> extractKeyWords(String queContent) {
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		EnglishStopTokenizerFactory stopFactory = new EnglishStopTokenizerFactory(tokenizerFactory);
		TfIdfDistance tfIdf = new TfIdfDistance(stopFactory);
		String[] words = queContent.split(" ");

		for(String s0 : words)
			tfIdf.handle(s0);
		
		Set<String> stopSet = stopFactory.stopSet() ;
		ObjectToCounterMap<String> map = tfIdf.termFrequencyVector(queContent);
		List<String> keys= map.keysOrderedByCountList();
		ArrayList<String> keyWords = new ArrayList<String>();
		List<Term> terms = new ArrayList<Term>();
		for(String word : keys){
			if(!word.matches("[^a-zA-Z]") && !stopSet.contains(word.toLowerCase())){ //大小写敏感
				int frequency = tfIdf.docFrequency(word) ;
				Term term = new Term();
				term.setWord(word);
				term.setDocFreq(frequency);
				terms.add(term);
			}
		}
		Collections.sort(terms, new Comparator<Term>() {
			@Override
			public int compare(Term o1, Term o2) {
				return o2.getDocFreq() - o1.getDocFreq();
			}
		});
		for(int i=0; i<10; i++) {
			keyWords.add(terms.get(i).getWord());
		}
		return keyWords;
	}
	
	/**
	 * 计算subStr在str中出现的次数
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
	 * 计算逻辑词出现频率
	 * @param text 学生作文
	 * @return 逻辑词频率
	 */
	public static int logicsCheck(String text) {
		int count = 0;
		if(logics.size() == 0) {
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(new File(FilePath.LOGICS));
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String text2 = null;
				while ((text2 = bufferedReader.readLine()) != null) {
					logics.add(text2.trim());
				}
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(String logic : logics) {
			count = count + countOcurrences(text, logic);
		}
		return count;
	}
	
	/**
	 * 计算误差单词数量（超过单词数或者是不足单词数）
	 * @param text 学生作文
	 * @param paramsConfiguration
	 * @return 单词数
	 */
	public static int contentLengthCheck(TextType textType, String text, ParamsConfig paramsConfiguration) {
		int count = 0;
		if(textType == TextType.LARGETEXT) {
			int MIN = paramsConfiguration.getMinWordNum();
			int contentLength = text.length();
			if(contentLength < MIN) {
				count = MIN - contentLength;
			}
		}
		else if(textType == TextType.TINYTEXT) {
			int MAX = paramsConfiguration.getMaxWordNum();
			int MIN = paramsConfiguration.getMinWordNum();
			int contentLength = text.length();
			if(contentLength < MIN) {
				count = MIN - contentLength;
			}
			else if(contentLength > MAX) {
				count = contentLength - MAX;
			}
		}
		return count;
	}
	
	/**
	 * 计算主题相关的词汇数量（暂时没有语料库，需要后期补充完善）
	 * @param words
	 * @param keyWords
	 * @return
	 */
	public static int keyWordsCheck(List<String> words, String[] keyWords) {
		int num = 0;
		if(keyWords.length > 0) {
			for(String word: words) {
				if(containedIn(word, keyWords)) {
					num++;
				}
			}
		}
		return num;
	}
	
	/**
	 * 判断单词在不在关键字集合中
	 * @param word
	 * @param keywords
	 * @return
	 */
	public static boolean containedIn(String word, String[] keywords) {
		boolean flag = false;
		for(String keyword: keywords) {
			String substr = dynamic4LCS(word, keyword);
			String max = word.length() > keyword.length() ? word : keyword;
			float rate = 0.67f;
			if(((float)substr.length() / max.length()) > rate) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 暴力破解获得两个字符串的最大公共子串
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String brute4LCS(String str1, String str2) {
		String max = (str1.length() > str2.length()) ? str1 : str2;
		String min = max.equals(str1)? str2 : str1;
		String currentMax = "";
		if(max.contains(min)) {
			currentMax = min;
			return currentMax;
		}
		for(int i = 0; i < min.length(); i++) {
			for(int j = min.length() - 1; j > i; j--) {
				String substr = min.substring(i, j);
				if(max.contains(substr)) {
					if(substr.length() > currentMax.length()) {
						currentMax = substr;
					}
				}
			}
		}
		return currentMax;
	}
	
	/**
	 * 动态规划获取两个字符串的最大公共子串
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String dynamic4LCS(String str1, String str2) {
		String substr = "";
		int len1 = str1.length(), len2 = str2.length();
		int maxPos = -1; //最大位置起点
		int maxLen = 0; //最大长度
		for(int i = 0; i < len1; i++) {
			for(int j = 0; j < len2; j++) {
				if(str1.charAt(i) == str2.charAt(j)) {
					int k = 1;
					while(true) {
 						if( (i+k) < len1 && (j+k) < len2 && str1.charAt(i+k) == str2.charAt(j+k)) {
							k++;
						}else{
							break;
						}
					}
					if(k > maxLen) {
						maxLen = k;
						maxPos = i;
					}
				}
			}
		}
		if(maxPos != -1) {
			substr = str1.substring(maxPos, maxPos + maxLen);
		}
		return substr;
	}

	/**
	 * 错误单词拼写建议
	 *
	 * @param word
	 * @return
	 */
	public static List<String> suggestWords(String word) {
		return enUS_Dict.suggest(word);
	}
}
