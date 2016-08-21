package com.qinjiangbo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.nlp.lm.ArrayEncodedNgramLanguageModel;
import edu.berkeley.nlp.lm.NgramLanguageModel.StaticMethods;
import edu.berkeley.nlp.lm.collections.BoundedList;

/**
 * ISAT语言模型
 * @author QinJiangbo
 *
 */
public class IsatkLanguageModel {

	/**
	 * 判断每个句子的分数(所有短句分数之和)
	 * @param sentence
	 * @param lm
	 * @return
	 */
	public static <T> float scoreSentence(final List<T> sentence, final ArrayEncodedNgramLanguageModel<T> lm) {
		final List<T> sentenceWithBounds = new BoundedList<T>(sentence, lm.getWordIndexer().getStartSymbol(), lm.getWordIndexer().getEndSymbol());

		final int lmOrder = lm.getLmOrder();
		float sentenceScore = 0.0f;
		
		for (int i = 1; i < lmOrder - 1 && i <= sentenceWithBounds.size() + 1; ++i) {
			final List<T> ngram = sentenceWithBounds.subList(-1, i);
			final float scoreNgram = lm.getLogProb(ngram);
			sentenceScore += scoreNgram;
		}
		
		for (int i = lmOrder - 1; i < sentenceWithBounds.size() + 2; ++i) {
			final List<T> ngram = sentenceWithBounds.subList(i - lmOrder, i);
			final float scoreNgram = lm.getLogProb(ngram);
			sentenceScore += scoreNgram;
		}
		return sentenceScore;
	}
	
	/**
	 * 判断每个句子的各个短语的分数
	 * @param sentence
	 * @param lm
	 * @return
	 */
	public static <T> Map<List<T>, Float> scoreNgrams(final List<T> sentence, final ArrayEncodedNgramLanguageModel<T> lm) {
		final List<T> sentenceWithBounds = new BoundedList<T>(sentence, lm.getWordIndexer().getStartSymbol(), lm.getWordIndexer().getEndSymbol());
		
		final int lmOrder = lm.getLmOrder();
		Map<List<T>, Float> ngramScores = new HashMap<List<T>, Float>();
		
		for(int i = 1; i < lmOrder - 1 && i <= sentenceWithBounds.size() + 1; ++i) {
			final List<T> ngram = sentenceWithBounds.subList(-1, i);
			final float scoreNgram = lm.getLogProb(ngram);
			ngramScores.put(ngram, scoreNgram);
		}
		
		for(int i = lmOrder - 1; i < sentenceWithBounds.size() + 2; ++i) {
			final List<T> ngram = sentenceWithBounds.subList(i - lmOrder, i);
			final float scoreNgram = lm.getLogProb(ngram);
			ngramScores.put(ngram, scoreNgram);
		}
		return ngramScores;
	}

	/**
	 * 计算短句的概率对数log(Prob)
	 * @param ngram
	 * @param lm
	 * @return
	 */
	public static <T> float getLogProb(final int[] ngram, final ArrayEncodedNgramLanguageModel<T> lm) {
		return lm.getLogProb(ngram, 0, ngram.length);
	}

	/**
	 * 计算短句的概率对数log(Prob)
	 * @param ngram
	 * @param lm
	 * @return
	 */
	public static <T> float getLogProb(final List<T> ngram, final ArrayEncodedNgramLanguageModel<T> lm) {
		final int[] ints = StaticMethods.toIntArray(ngram, lm);
		return lm.getLogProb(ints, 0, ints.length);

	}

}
