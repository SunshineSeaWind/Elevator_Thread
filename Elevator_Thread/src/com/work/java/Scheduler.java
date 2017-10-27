package com.work.java;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

class Scheduler extends Thread{
	private List<Request> temp;
	private Elevator[] elevator=new Elevator[3];
	private Request request=null;
	public Scheduler(List<Request> temp,BufferedWriter bw){
		this.temp=temp;
		elevator[0]=new Elevator(1,bw);
		elevator[1]=new Elevator(2,bw);
		elevator[2]=new Elevator(3,bw);
	}
	@Override
	public void run() {
		elevator[0].start();
		elevator[1].start();
		elevator[2].start();
		while(true){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			addRequestToElevator();
		}
	}
	private void addRequestToElevator(){
		Elevator tempEle;
		for(int i=0;i<temp.size();i++){
			//ER����ֱ����ӵ���Ӧ����
			Request req=temp.get(i);
			if(req.getType().equals("ER")){
				elevator[req.getInWhichElevator()-1].receiveRequset(req);
				temp.remove(req);
				i--;
			}
			else if(req.getType().equals("FR")){
				tempEle=findElevator(req);
				//������ڸõ����ܶ����������Ӧ������ӣ�����continue
				if(tempEle!=null){
					elevator[tempEle.getid()-1].receiveRequset(req);
					temp.remove(req);
					i--;
				}
				else{
					continue;
				}
			}
		}
	}
	private Elevator findElevator(Request req){
		List<Elevator> tempElevator=new ArrayList<Elevator>();
		//��������Ӵ�
		for(int i=0;i<3;i++){
			if(req.getDirection().equals("UP") && req.getNum()>elevator[i].getE_n() && req.getNum()<=elevator[i].getN() 
					&&req.getDirection().equals(elevator[i].getStatus() )){
					tempElevator.add(elevator[i]);
			}
		else if(req.getDirection().equals("DOWN") && req.getNum()<elevator[i].getE_n() && req.getNum()>=elevator[i].getN() 
				&& req.getDirection().equals(elevator[i].getStatus())){
				tempElevator.add(elevator[i]);
			}
		}
		//��������Ӵ��������ж�����㣬�ҵ��ۼ��˶�����С�ģ������ѡ��Ψһһ�������Ӵ��ĵ���
		if(!tempElevator.isEmpty()){
			return findMin(tempElevator);
		}
		//���û���Ӵ�����ѡ������Ӧ���ۼ��˶�����С��
		else{
			for(int i=0;i<3;i++){
				if(!elevator[i].getIsBusy()){
					tempElevator.add(elevator[i]);
				}
			}
			if(!tempElevator.isEmpty()){
				return findMin(tempElevator);
			}
			//û�п�����Ӧ�ģ�ֱ�ӷ���null
			else{
				return null;
			}
		}
	}
	private Elevator findMin(List<Elevator> tempElevator){
		Elevator tempelevator=tempElevator.get(0);
		if(tempElevator.size()==1){
			return tempelevator;
		}
		else{
			for(int i=1;i<tempElevator.size();i++){
				if(tempElevator.get(i).getRunFloorNum()<tempelevator.getRunFloorNum()){
					tempelevator=tempElevator.get(i);
				}
			}
			return tempelevator;
		}
	}
}