package com.work.java;

class ElevatorRequest{
	private String preString;
	private String type;
	private int toNum;
	private int inWhichElevator;
	private double T;
	public ElevatorRequest(String preString,String type,String string,int toNum, double T) {
		this.preString=preString;
		this.type=type;
		this.toNum = toNum;
		this.inWhichElevator=Integer.valueOf(""+string.charAt(1));
		this.T = T;
	}
	public String getType() {
		return type;
	}
	public int getInWhichElevator() {
		return inWhichElevator;
	}
	public int getToNum() {
		return toNum;
	}
	public double getT() {
		return T;
	}
	public String getPreString() {
		return preString;
	}
}
