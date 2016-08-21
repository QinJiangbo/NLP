package com.qinjiangbo.util;

public class ParamsConfig {
	
	private int overLengthN;
	private int belowLengthN;
	private int topNwords;
	private float logicMatchRate;
	private int minWordNum;
	private int maxWordNum;
	private int minBorderNum;
	private int maxBorderNum;
	private float themeMatchRate;
	private int berkelyScoreHigh;
	private int berkelyScoreLow;
	private float sentenceWordRate;
	private int sentenceWordNum;
	private float sentenceSimilarity;
	private float sentenceWordMatchRate;
	
	public ParamsConfig() {
		init();
	}
	
	/**
	 * 初始化各个参数
	 */
	private void init() {
		overLengthN = 7;
		belowLengthN = 4;
		topNwords = 20;
		logicMatchRate = 0.20f;
		maxWordNum = 225;
		minWordNum = 115;
		maxBorderNum = 450;
		minBorderNum = 0;
		themeMatchRate = 0.20f;
		berkelyScoreHigh = -26;
		berkelyScoreLow = berkelyScoreHigh * 3;
		sentenceWordRate = 0.50f;
		sentenceSimilarity = 0.70f;
		sentenceWordMatchRate = 0.20f;
		sentenceWordNum = 7;
	}
	
	public int getOverLengthN() {
		return overLengthN;
	}

	public void setOverLengthN(int overLengthN) {
		this.overLengthN = overLengthN;
	}

	public int getBelowLengthN() {
		return belowLengthN;
	}

	public void setBelowLengthN(int belowLengthN) {
		this.belowLengthN = belowLengthN;
	}

	public int getTopNwords() {
		return topNwords;
	}

	public void setTopNwords(int topNwords) {
		this.topNwords = topNwords;
	}

	public float getLogicMatchRate() {
		return logicMatchRate;
	}

	public void setLogicMatchRate(float logicMatchRate) {
		this.logicMatchRate = logicMatchRate;
	}

	public int getMinWordNum() {
		return minWordNum;
	}

	public void setMinWordNum(int minWordNum) {
		this.minWordNum = minWordNum;
	}

	public int getMaxWordNum() {
		return maxWordNum;
	}

	public void setMaxWordNum(int maxWordNum) {
		this.maxWordNum = maxWordNum;
	}
	
	public int getMinBorderNum() {
		return minBorderNum;
	}

	public void setMinBorderNum(int minBorderNum) {
		this.minBorderNum = minBorderNum;
	}

	public int getMaxBorderNum() {
		return maxBorderNum;
	}

	public void setMaxBorderNum(int maxBorderNum) {
		this.maxBorderNum = maxBorderNum;
	}

	public float getThemeMatchRate() {
		return themeMatchRate;
	}

	public void setThemeMatchRate(float themeMatchRate) {
		this.themeMatchRate = themeMatchRate;
	}
	
	public float getSentenceWordRate() {
		return sentenceWordRate;
	}

	public void setSentenceWordRate(float sentenceWordRate) {
		this.sentenceWordRate = sentenceWordRate;
	}

	public float getSentenceSimilarity() {
		return sentenceSimilarity;
	}

	public void setSentenceSimilarity(float sentenceSimilarity) {
		this.sentenceSimilarity = sentenceSimilarity;
	}

	public float getSentenceWordMatchRate() {
		return sentenceWordMatchRate;
	}

	public void setSentenceWordMatchRate(float sentenceWordMatchRate) {
		this.sentenceWordMatchRate = sentenceWordMatchRate;
	}

	public int getSentenceWordNum() {
		return sentenceWordNum;
	}

	public void setSentenceWordNum(int sentenceWordNum) {
		this.sentenceWordNum = sentenceWordNum;
	}
	
	public int getBerkelyScoreHigh() {
		return berkelyScoreHigh;
	}

	public void setBerkelyScoreHigh(int berkelyScoreHigh) {
		this.berkelyScoreHigh = berkelyScoreHigh;
	}

	public int getBerkelyScoreLow() {
		return berkelyScoreLow;
	}

	public void setBerkelyScoreLow(int berkelyScoreLow) {
		this.berkelyScoreLow = berkelyScoreLow;
	}


}
