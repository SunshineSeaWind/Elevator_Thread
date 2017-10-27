package com.work.java;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RequestThread extends Thread{
	private  BufferedWriter bw;
	List<Request> temp=new ArrayList<Request>();
	public RequestThread(List<Request> temp,BufferedWriter bw){
		this.temp=temp;
		this.bw=bw;
	}
	public void run() {
		Scanner scanner=new Scanner(System.in);
		while(scanner.hasNextLine()){
			String string=scanner.nextLine();
			DecimalFormat dcmFmt = new DecimalFormat("0.0");
			double inputTime=Double.valueOf(dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0));
			//去除空格
			string=string.replaceAll(" ", "");
			//run结束程序
			if(string.equals("run")){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
				break;
			}
			String [] str_1=string.split(";");
			String regEx = "\\(FR,[+]?0*((([2-9]|(1[0-9])),(UP|DOWN))|(20,DOWN)|(1,UP))\\)|\\(ER,#[1-3],[+]?0*(([1-9])|(1[0-9])|20)\\)";
			// 编译正则表达式
			Pattern pattern = Pattern.compile(regEx);
			int count=0;
			for(int i=0;i<str_1.length;i++){
				Matcher matcher = pattern.matcher(str_1[i]);
				if(matcher.matches() && count<10){
					count++;
			    	string=matcher.group().substring(1,str_1[i].length()-1);
			    	String [] str_2=string.split(",");
			    	Request request;
			    	//添加楼层请求对象
			    	//(preString,FR,Floor,UP/DOWN,T)
					   if(str_2.length==3 && str_2[0].equals("FR") ){
						   FloorRequest floorrequest=new FloorRequest(str_1[i],str_2[0],Integer.valueOf(str_2[1]), str_2[2], inputTime);
						   request=new Request(floorrequest);
						   temp.add(request);
					   }
					 //添加电梯请求对象
					   else if (str_2.length==3 && str_2[0].equals("ER")) {
						   //(preString,ER,#num,Floor,T)
						   ElevatorRequest elevatorrequest=new ElevatorRequest(str_1[i],str_2[0], str_2[1], Integer.valueOf(str_2[2]),inputTime);
						   request=new Request(elevatorrequest);
						   temp.add(request);
					}
					   //对象不能添加
					   else{
						   String tempstring=System.currentTimeMillis()+":INVALID["+str_1[i]+","+inputTime+"]"+System.getProperty("line.separator");
//						   System.out.print(tempstring);
						   try {
							bw.write(tempstring);
							bw.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					   }
			    }
				//正则表达不能完全匹配
				else{
					String tempstring=System.currentTimeMillis()+":INVALID["+str_1[i]+","+inputTime+"]"+System.getProperty("line.separator");
//					System.out.print(tempstring);
					  try {
							bw.write(tempstring);
							bw.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}    
		}
	}
}