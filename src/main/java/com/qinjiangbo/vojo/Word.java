package com.qinjiangbo.vojo;

import java.util.List;

public class Word {
	private String content;
	private int paraNum; //段落号
	private int orderNum; //段落中第几句
	private List<String> wordProps; //单词词性
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getParaNum() {
		return paraNum;
	}
	
	public void setParaNum(int paraNum) {
		this.paraNum = paraNum;
	}
	
	public int getOrderNum() {
		return orderNum;
	}
	
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	
	public List<String> getWordProps() {
		return wordProps;
	}
	
	public void setWordProps(List<String> wordProps) {
		this.wordProps = wordProps;
	}
}
