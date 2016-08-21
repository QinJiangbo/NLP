package com.qinjiangbo.util;

import com.qinjiangbo.vojo.WeightModel;

public class WeightConfig {
	
	private WeightModel conventions; // 拼写和语法
	private WeightModel wordChoice; // 词汇运用
	private WeightModel organization; // 组织结构
	private WeightModel ideasContent; // 内容与思想
	private WeightModel flucency; // 流畅度
	private WeightModel voice; // 文体
	
	private WeightModel subSentence; // 语法正确率
	private WeightModel subWord; // 单词正确率
	private WeightModel subOverN; // 超过n个字母的单词数量占全文比例正比例
	private WeightModel subBelowN; // 低于过n个字母的单词数量占全文比例反比例
	private WeightModel subRepeat; // 全文单词重复率是否
	private WeightModel subLogic; // 全文单词匹配逻辑词词库匹配率	
	private WeightModel subWordCount; // 篇幅长度：全文单词数量[分两种情况]
	private WeightModel subWordMatch; // 全文单词匹配对应词库匹配率[模糊匹配]
	private WeightModel subSentenceMatch; // 全文句子和预设内容库要点匹配率
	private WeightModel subBerkelyScore; // 全文超过N个单词的句子所占比例
	
	public WeightConfig() {
		init();
	}
	
	/**
	 * 初始化各个评价模块
	 */
	private void init() {
		//初始化大类
		conventions = new WeightModel("conventions", "拼写和语法", 0.30f);
		wordChoice = new WeightModel("wordChoice", "词汇运用", 0.30f);
		organization = new WeightModel("organization", "组织结构", 0.20f);
		ideasContent = new WeightModel("ideasContent", "内容与思想", 0.20f);
		flucency = new WeightModel("flucency", "流畅度", 0.3f);
		voice = new WeightModel("voice", "文体", 0);
		
		//初始化子类(1)
		subSentence = new WeightModel("subSentence", "语法正确率", 0.60f);
		subWord = new WeightModel("subWord", "单词正确率", 0.40f);
		//初始化子类(2)
		subOverN = new WeightModel("subOverN", "超过n个字母的单词数量占全文比例正比例", 0.25f);
		subBelowN = new WeightModel("subBelowN", "低于过n个字母的单词数量占全文比例反比例", 0.25f);
		subRepeat = new WeightModel("subRepeat", "全文单词重复率是否", 0.50f);
		//初始化子类(3)
		subLogic = new WeightModel("subLogic", "全文单词匹配逻辑词词库匹配率", 0.40f);
		subWordCount = new WeightModel("subWordCount", "篇幅长度：全文单词数量", 0.60f);
		//初始化子类(4)
		subWordMatch = new WeightModel("subWordMatch", "全文单词匹配对应词库匹配率[模糊匹配]", 1.0f);
		//初始化子类(5)
		subBerkelyScore = new WeightModel("subBerkelyScore", "伯克利句子分值", 0.5f);
		subSentenceMatch = new WeightModel("subSentenceMatch", "全文句子和预设内容库要点匹配率", 0.5f);
	}
	
	public WeightModel getConventions() {
		return conventions;
	}

	public void setConventions(WeightModel conventions) {
		this.conventions = conventions;
	}

	public WeightModel getWordChoice() {
		return wordChoice;
	}

	public void setWordChoice(WeightModel wordChoice) {
		this.wordChoice = wordChoice;
	}

	public WeightModel getOrganization() {
		return organization;
	}

	public void setOrganization(WeightModel organization) {
		this.organization = organization;
	}

	public WeightModel getIdeasContent() {
		return ideasContent;
	}

	public void setIdeasContent(WeightModel ideasContent) {
		this.ideasContent = ideasContent;
	}

	public WeightModel getFlucency() {
		return flucency;
	}

	public void setFlucency(WeightModel flucency) {
		this.flucency = flucency;
	}

	public WeightModel getVoice() {
		return voice;
	}

	public void setVoice(WeightModel voice) {
		this.voice = voice;
	}

	public WeightModel getSubSentence() {
		return subSentence;
	}

	public void setSubSentence(WeightModel subSentence) {
		this.subSentence = subSentence;
	}

	public WeightModel getSubWord() {
		return subWord;
	}

	public void setSubWord(WeightModel subWord) {
		this.subWord = subWord;
	}

	public WeightModel getSubOverN() {
		return subOverN;
	}

	public void setSubOverN(WeightModel subOverN) {
		this.subOverN = subOverN;
	}

	public WeightModel getSubBelowN() {
		return subBelowN;
	}

	public void setSubBelowN(WeightModel subBelowN) {
		this.subBelowN = subBelowN;
	}

	public WeightModel getSubRepeat() {
		return subRepeat;
	}

	public void setSubRepeat(WeightModel subRepeat) {
		this.subRepeat = subRepeat;
	}

	public WeightModel getSubLogic() {
		return subLogic;
	}

	public void setSubLogic(WeightModel subLogic) {
		this.subLogic = subLogic;
	}

	public WeightModel getSubWordCount() {
		return subWordCount;
	}

	public void setSubWordCount(WeightModel subWordCount) {
		this.subWordCount = subWordCount;
	}

	public WeightModel getSubWordMatch() {
		return subWordMatch;
	}

	public void setSubWordMatch(WeightModel subWordMatch) {
		this.subWordMatch = subWordMatch;
	}

	public WeightModel getSubSentenceMatch() {
		return subSentenceMatch;
	}

	public void setSubSentenceMatch(WeightModel subSentenceMatch) {
		this.subSentenceMatch = subSentenceMatch;
	}
	
	public WeightModel getSubBerkelyScore() {
		return subBerkelyScore;
	}

	public void setSubBerkelyScore(WeightModel subBerkelyScore) {
		this.subBerkelyScore = subBerkelyScore;
	}
}
