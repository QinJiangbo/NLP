package com.qinjiangbo.vojo;

public class Sentence {
	
	private String content;
	private int paraNum; //段落号
	private int orderNum; //段落中第几句
	
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
}
