package com.work.java;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class Elevator extends Thread{
	//���ܵ������ഫ����ָ��
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
		//�����еƶ����ʱ������������
		if(!isBusy){
			mainRequest=request;
			//�������������ɾ��
			list.remove(list.size()-1);
			n=mainRequest.getNum();
			isBusy=true;
			if(request.getType().equals("ER")){
				ElevatorButton[request.getNum()]=1;
			}
			else if(request.getType().equals("FR")){
				floorButton[request.getNum()][request.getDirection().equals("UP") ? 0 : 1]=1;
			}
			//�ı����״̬
			status=n<e_n ? "DOWN" : n>e_n ? "UP" : "STILL";
		}
		else{
			if(request.getType().equals("ER")){
				//SAMEָ���
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
				//���ð�ťΪ��
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
		//��������Ǻ͵���ԭ��¥��һ��,�������е�ͬ������(¥��ֻҪ��ͬ��������ɴ���)
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
			//�ذ�ť
			ElevatorButton[e_n]=0;
			floorButton[e_n][0]=0;
			floorButton[e_n][1]=0;
			//���ִ�����STILL�����е�����,��ô������Ӧ�þͱ�����Ǹ����ŵĵ�����
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
				//������������з�������¥����ߵ��ݵİ�ť���ţ���ͣ�����
				//һ��ʼ��¥��ճ���ʱ�����Ӵ�������Ϊͬ¥�㣬����˯6�룬Ȼ���ڴ�ӡ
				if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n==starte_n){
					//����6��
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long time=System.currentTimeMillis();
					String string="/(#"+id+","+e_n+",STILL,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
					//�����б���������Ӵ�����
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
					//�ذ�ť
					ElevatorButton[e_n]=0;
					floorButton[e_n][0]=0;
					floorButton[e_n][1]=0;
				}
				else{
					//һ��ʼ��¥��ճ���ʱû���Ӵ����󣬼���Ϊͬ¥�㣬֮����Ӵ��ȴ�ӡ����˯6��
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
						//�����б���������Ӵ�����
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
						//����6��
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//�ذ�ť
						ElevatorButton[e_n]=0;
						floorButton[e_n][0]=0;
						floorButton[e_n][1]=0;
					}
				}
			}
			//���ִ�����������е�����,��ô������Ӧ�þͱ�����Ǹ����ŵĵ�����
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
				//����½������з�������¥����ߵ��ݵİ�ť���ţ���ͣ�����
				//һ��ʼ��¥��ճ���ʱ�����Ӵ�������Ϊͬ¥�㣬����˯6�룬Ȼ���ڴ�ӡ
				if((ElevatorButton[e_n]==1 || floorButton[e_n][0]==1 || floorButton[e_n][1]==1) && e_n==starte_n){
					//����6��
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long time=System.currentTimeMillis();
					String string="/(#"+id+","+e_n+",STILL,"+runFloorNum+","+dcmFmt.format((System.currentTimeMillis()-Work_main.systemBeginTime)/1000.0)+")"+System.getProperty("line.separator");
					//�����б���������Ӵ�����
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
					//�ذ�ť
					ElevatorButton[e_n]=0;
					floorButton[e_n][0]=0;
					floorButton[e_n][1]=0;
				}
				else{
					//һ��ʼ��¥��ճ���ʱû���Ӵ����󣬼���Ϊͬ¥�㣬֮����Ӵ��ȴ�ӡ����˯6��
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
						//�����б���������Ӵ�����
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
						//����6��
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//�ذ�ť
						ElevatorButton[e_n]=0;
						floorButton[e_n][0]=0;
						floorButton[e_n][1]=0;
					}
				}
			}
			//���ִ�����������е�����,��ô������Ӧ�þͱ�����Ǹ����ŵĵ�����
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