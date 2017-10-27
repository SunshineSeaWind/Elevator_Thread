package com.work.java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Work_main {
	public static long systemBeginTime=System.currentTimeMillis();
	public static void main(String[] args) {
		try {
		//所有命令全在temp里面
		List<Request> temp=new ArrayList<Request>();
		BufferedWriter bw=new BufferedWriter(new FileWriter("result.txt"));
		RequestThread requestThread=new RequestThread(temp,bw);
		Scheduler scheduler=new Scheduler(temp,bw);
		requestThread.start();
		scheduler.start();
		} catch (Exception e) {
			System.out.println("出现错误！");
		}
	}	
}








