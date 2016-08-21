package com.qinjiangbo.vojo;

public class WeightModel {
	
	private String enName;
	private String cnName;
	private float weight;
	
	public WeightModel(String enName, String cnName, float weight) {
		this.enName = enName;
		this.cnName = cnName;
		this.weight = weight;
	}
	
	public String getEnName() {
		return enName;
	}
	
	public void setEnName(String enName) {
		this.enName = enName;
	}
	
	public String getCnName() {
		return cnName;
	}
	
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
}
