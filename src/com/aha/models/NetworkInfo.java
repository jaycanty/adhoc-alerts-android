package com.aha.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;


public class NetworkInfo {
	
	
	private static NetworkInfo instance = null;
	private boolean networkUp;
	private boolean deviceInitiated;
	private boolean joined;
	private boolean acknowledged = false;
	
	private int myIP;
	
	//private int GlobalRank;

	
	public HashMap<Integer, Vector<DataObject>> conversations;
	public Vector<DataObject> broadCasts;
	//public HashMap<String, String> network;
	public Vector<NetworkNode> network;
	public Vector<Integer> myNetwork;
	private int[] initIPList = {1,2,3,4,5,6,7,8,9,10};
	
	protected NetworkInfo(){
		
		networkUp = false;
		deviceInitiated = false;
		joined = false;
		conversations = new HashMap<Integer, Vector<DataObject>>(); //(HashMap<String, Vector<DataObject>>)Collections.synchronizedMap(new HashMap<String, Vector<DataObject>>());
		//network = new HashMap<String, String>(); //(HashMap<String, String>)Collections.synchronizedMap(new HashMap<String, String>());
		network = new Vector<NetworkNode>();
		broadCasts = new Vector<DataObject>();
	}	
	
	
	public synchronized int getInitIP()
	{		
		Random r = new Random();
		int ip = initIPList[r.nextInt(9) + 1];
		this.myIP = ip;
		return ip;
	}
	
	
	public synchronized static NetworkInfo getInstance() {
	      if(instance == null) {
	         instance = new NetworkInfo();
	      }
	      return instance;
	}	
	
	
	public synchronized int getMyIP() {
		return myIP;
	}


	public synchronized void setMyIP(int myIP) {
		this.myIP = myIP;
	}
	
	public synchronized boolean isNetworkUp() {
		return networkUp;
	}

	public synchronized void setNetworkUp(boolean networkUp) {
		this.networkUp = networkUp;
	}

	public synchronized boolean isDeviceInitiated() {
		return deviceInitiated;
	}

	public synchronized void setDeviceInitiated(boolean deviceInitiated) {
		this.deviceInitiated = deviceInitiated;
	}


	public boolean isJoined() {
		return joined;
	}


	public void setJoined(boolean joined) {
		this.joined = joined;
	}	
	
	public synchronized boolean isAcknowledged() {
		return acknowledged;
	}

	public synchronized void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}	

}
