package com.work.java;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class Elevator extends Thread{
	//接受调度器类传来的指令
	private Request request=null,mainRequest=null;
	private int e_n=1,n=1;
	private String status="STILL";
	private int runFloorNum=0;
	private boolean isBusy=false;
	private int id;
	private int ElevatorButton[]=new int [21];
	private int floorButton[][]=new int [21][2];
	private List<Request> list=new ArrayList<Request>();
	private DecimalFormat dcmFmt = new DecimalFormat("0.0");
	private BufferedWriter bw;
	public Elevator(int id,BufferedWriter bw){
		this.id=id;
		this.bw=bw;
	}
	public int getid(){
		return id;
	}
	public Request getMainRequest() {
		return mainRequest;
	}
	public int getE_n() {
		return e_n;
	}
	public int getN() {
		return n;
	}
	public String getStatus() {
		return status;
	}
	public int getRunFloorNum() {
		return runFloorNum;
	}
	public boolean getIsBusy() {
		return isBusy;
	}
	@Override
	public void run() {
		while(true){
			if(!isBusy){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			else{
				doWork();
			}
		}
	}
	public void receiveRequset(Request request){
		this.request=request;
		list.add(this.request);
		setEleData();
	}
	private void setEleData(){
		//当所有灯都灭的时候，设置主请求
		if(!isBusy){
			mainRequest=request;
			//将加入的主请求删除
			list.remove(list.size()-1);
			n=mainRequest.getNum();
			isBusy=true;
			if(request.getType().equals("ER")){
				ElevatorButton[request.getNum()]=1;
			}
			else if(request.getType().equals("FR")){
				floorButton[request.getNum()][request.getDirection().equals("UP") ? 0 : 1]=1;
			}
			//改变电梯状态
			status=n<e_n ? "DOWN" : n>e_n ? "UP" : "STILL";
		}
		else{
			if(request.getType().equals("ER")){
				//SAME指令处理
				if(ElevatorButton[request.getNum()]==1){
					String string=System.currentTimeMillis()+":SAME:["+request+"]"+System.getProperty("line.separator");
//					System.out.print(string);
					try {
						bw.write(string);
						bw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					list.remove(request);
				}
				//设置按钮为亮
				else{
					ElevatorButton[request.getNum()]=1;
				}
			}
			else if(request.getType().equals("FR")){
				if(floorButton[request.getNum()][request.getDirection().equals("UP") ? 0 : 1]==1){
					String string=System.currentTimeMillis()+":SAME:["+request+"]"+System.getProperty("line.separator");
//					System.out.print(string);
					try {
						bw.write(string);
						bw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					list.remove(request);
				}
				else{
					floorButton[request.getNum()][request.getDirection().equals("UP") ? 0 : 1]=1;
				}
			}
		}
	}
	public void doWork(){
		//主请求就是和电梯原来楼层一样,处理所有的同层请求(楼层只要相同的请求均可处理)
		if(status.equals("STILL")){
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long time=System.currentTimeMillis();
			String string="/(#"+id+","+e_n+",STILL,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
//			System.out.print(time+":["+mainRequest+"]"+string);
			try {
				bw.write(time+":["+mainRequest+"]"+string);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int i=0;i<list.size();i++){
				if(list.get(i).getNum()==e_n){
//					System.out.print(time+":["+list.get(i)+"]"+string);
					try {
						bw.write(time+":["+list.get(i)+"]"+string);
						bw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					list.remove(i);
					i--;
				}
			}
			mainRequest=null;
			isBusy=false;
			//关按钮
			ElevatorButton[e_n]=0;
			floorButton[e_n][0]=0;
			floorButton[e_n][1]=0;
			//如果执行完该STILL请求还有灯亮着,那么主请求应该就变成了那个亮着的灯请求
			if(!list.isEmpty()){
				mainRequest=list.get(0);
				n=mainRequest.getNum();
				list.remove(0);
				isBusy=true;
				status=n<e_n ? "DOWN" : n>e_n ? "UP" : "STILL";
			}
			else{
				mainRequest=null;
				isBusy=false;
			}
		}
		else if(status.equals("UP")){
			int starte_n=e_n;
			while(e_n<mainRequest.getNum()){
				//如果上升过程中发现所在楼层或者电梯的按钮亮着，则停靠输出
				//一开始的楼层刚出发时就有捎带请求，且为同楼层，则先睡6秒，然后在打印
				if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n==starte_n){
					//休眠6秒
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long time=System.currentTimeMillis();
					String string="/(#"+id+","+e_n+",STILL,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
					//遍历列表，输出所有捎带请求
					for(int i=0;i<list.size();i++){
						if(list.get(i).getNum()==e_n){
//							System.out.print(time+":["+list.get(i)+"]"+string);
							try {
								bw.write(time+":["+list.get(i)+"]"+string);
								bw.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							list.remove(i);
							i--;
						}
					}
					//关按钮
					ElevatorButton[e_n]=0;
					floorButton[e_n][0]=0;
					floorButton[e_n][1]=0;
				}
				else{
					//一开始的楼层刚出发时没有捎带请求，即不为同楼层，之后的捎带先打印，再睡6秒
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					e_n++;
					runFloorNum++;
					if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n!=starte_n){
						long time=System.currentTimeMillis();
						String string="/(#"+id+","+e_n+",UP,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
						if(e_n==mainRequest.getNum()){
//							System.out.print(time+":["+mainRequest+"]"+string);
							try {
								bw.write(time+":["+mainRequest+"]"+string);
								bw.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//遍历列表，输出所有捎带请求
						for(int i=0;i<list.size();i++){
							if(list.get(i).getNum()==e_n){
//								System.out.print(time+":["+list.get(i)+"]"+string);
								try {
									bw.write(time+":["+list.get(i)+"]"+string);
									bw.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
								list.remove(i);
								i--;
							}
						}
						//休眠6秒
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//关按钮
						ElevatorButton[e_n]=0;
						floorButton[e_n][0]=0;
						floorButton[e_n][1]=0;
					}
				}
			}
			//如果执行完主请求还有灯亮着,那么主请求应该就变成了那个亮着的灯请求
			if(!list.isEmpty()){
				mainRequest=list.get(0);
				n=mainRequest.getNum();
				list.remove(0);
				isBusy=true;
				status=n<e_n ? "DOWN" : n>e_n ? "UP" : "STILL";
			}
			else{
				mainRequest=null;
				isBusy=false;
			}
		}
		else if(status.equals("DOWN")){
			int starte_n=e_n;
			while(e_n>mainRequest.getNum()){
				//如果下降过程中发现所在楼层或者电梯的按钮亮着，则停靠输出
				//一开始的楼层刚出发时就有捎带请求，且为同楼层，则先睡6秒，然后在打印
				if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n==starte_n){
					//休眠6秒
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long time=System.currentTimeMillis();
					String string="/(#"+id+","+e_n+",STILL,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
					//遍历列表，输出所有捎带请求
					for(int i=0;i<list.size();i++){
						if(list.get(i).getNum()==e_n){
//							System.out.print(time+":["+list.get(i)+"]"+string);
							try {
								bw.write(time+":["+list.get(i)+"]"+string);
								bw.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							list.remove(i);
							i--;
						}
					}
					//关按钮
					ElevatorButton[e_n]=0;
					floorButton[e_n][0]=0;
					floorButton[e_n][1]=0;
				}
				else{
					//一开始的楼层刚出发时没有捎带请求，即不为同楼层，之后的捎带先打印，再睡6秒
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					e_n--;
					runFloorNum++;
					if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n!=starte_n){
						long time=System.currentTimeMillis();
						String string="/(#"+id+","+e_n+",DOWN,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
						if(e_n==mainRequest.getNum()){
//							System.out.print(time+":["+mainRequest+"]"+string);
							try {
								bw.write(time+":["+mainRequest+"]"+string);
								bw.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//遍历列表，输出所有捎带请求
						for(int i=0;i<list.size();i++){
							if(list.get(i).getNum()==e_n){
//								System.out.print(time+":["+list.get(i)+"]"+string);
								try {
									bw.write(time+":["+list.get(i)+"]"+string);
									bw.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
								list.remove(i);
								i--;
							}
						}
						//休眠6秒
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//关按钮
						ElevatorButton[e_n]=0;
						floorButton[e_n][0]=0;
						floorButton[e_n][1]=0;
					}
				}
			}
			//如果执行完主请求还有灯亮着,那么主请求应该就变成了那个亮着的灯请求
			if(!list.isEmpty()){
				mainRequest=list.get(0);
				n=mainRequest.getNum();
				list.remove(0);
				isBusy=true;
				status=n<e_n ? "DOWN" : n>e_n ? "UP" : "STILL";
			}
			else{
				mainRequest=null;
				isBusy=false;
			}
		}
	}
}