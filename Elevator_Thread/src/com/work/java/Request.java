package com.work.java;

class Request{
	private String preString;
	private String type;
	private int num;
	private String direction;
	private int inWhichElevator;
	private double T;
	public Request(FloorRequest floor){
		this.preString=floor.getPreString();
		this.type=floor.getType();
		this.num=floor.getFromNum();
		this.direction=floor.getDirection();
		this.T=floor.getT();
	}
	public Request(ElevatorRequest elevator){
		this.preString=elevator.getPreString();
		this.type=elevator.getType();
		this.num=elevator.getToNum();
		this.direction="";
		this.inWhichElevator=elevator.getInWhichElevator();
		this.T=elevator.getT();
	}
	public int getNum() {
		return num;
	}
	public String getDirection() {
		return direction;
	}
	public double getT() {
		return T;
	}
	public String getType() {
		return type;
	}
	public int getInWhichElevator(){
		return inWhichElevator;
	}
	public String getPreString() {
		return preString;
	}
	public String toString() {
		return preString.substring(1,preString.length()-1)+","+T;
	}
}
