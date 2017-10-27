package com.work.java;

class FloorRequest{
	private String preString;
	private String type;
	private  int fromNum;
	private String direction;
	private double T;
	public FloorRequest(String preString,String type,int num,String direction,double T){
		this.preString=preString;
		this.type=type;
		this.fromNum=num;
		this.direction=direction;
		this.T=T;
	}
	public String getType() {
		return type;
	}
	public int getFromNum() {
		return fromNum;
	}
	public String getDirection() {
		return direction;
	}
	public String getPreString() {
		return preString;
	}
	public double getT() {
		return T;
	}
}