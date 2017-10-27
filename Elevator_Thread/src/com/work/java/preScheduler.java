package com.work.java;

import java.util.Queue;

class preScheduler implements preSrc{
	private double T=0.0;
	private Request request_before=null,request=null;
	public void do_Schedule(Queue<Request> requestQuene){
		while(!requestQuene.isEmpty()){
			request=requestQuene.remove();
			if(request_before==null){
				T=T+Math.abs((request.getNum()-1))*0.5+1;
				String dir=request.getNum()>1 ? "UP" : "STILL";
				double t=dir.equals("STILL") ? T : T-1;
				System.out.println("("+request.getNum()+","+dir+","+t+")");
				request_before=request;
				continue;
			}
			else {
				T=((request.getT()<T)? T : request.getT())+Math.abs((request.getNum()-request_before.getNum()))*0.5+1;
			}
			request_before=request;
		}
	}
}
