package com.qinjiangbo.service;

import java.util.ArrayList;
import java.util.List;

import com.qinjiangbo.util.CorpusCache;
import com.qinjiangbo.util.NotProcessedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PreProcessor {
	
	private List<String> Sentences = new ArrayList<String>();
	private List<String> Words = new ArrayList<String>();
	
	public PreProcessor() {}
	
	/**
	 * 预处理学生作文
	 * @param text 学生作文
	 * @return true-处理正常;false-处理异常
	 */
	public boolean process(String text) {
		//处理文章
		Sentences = CorpusCache.splitText(text);
		Words = splitSentences(Sentences);
		return true;
	}
	
	/**
	 * 将句子切分成一个个独立的单词
	 * @param sentences 句子集合
	 * @return 单词集合
	 */
	private List<String> splitSentences(List<String> sentences) {
		List<String> words = new ArrayList<String>();
		for(String sentence: sentences) {
			//需要加上点\',某些短语是有意义的
			sentence = sentence.replaceAll("-[ ]", "");
			sentence = sentence.replaceAll("[^a-zA-z0-9\']", " ");
			sentence = sentence.replaceAll(" +", " ");
			String[] wds = sentence.split(" ");
			for(String word : wds) {
				if(!StringUtils.isEmpty(word)) {
					words.add(word);
				}
			}
		}
		return words;
	}
	
	/**
	 * 清空预处理对象
	 */
	public void flush() {
		Sentences.clear();
		Words.clear();
	}

    public List<String> getSentences() throws NotProcessedException {
        if(Sentences.size() == 0) {
            throw new NotProcessedException("Illegal Access! PreProcess the text first!");
        }
		return Sentences;
	}

	public void setSentences(List<String> sentences) {
		Sentences = sentences;
	}

    public List<String> getWords() throws NotProcessedException {
        if(Words.size() == 0) {
            throw new NotProcessedException("Illegal Access! PreProcess the text first!");
        }
		return Words;
	}

	public void setWords(List<String> words) {
		Words = words;
	}
	
}