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
	
	private String myIP;

	
	public HashMap<String, Vector<DataObject>> conversations;
	//public HashMap<String, String> network;
	public Vector<String> network;
	
	public Vector<String> initIPList; 
	
	protected NetworkInfo(){
		
		networkUp = false;
		deviceInitiated = false;
		joined = false;
		conversations = new HashMap<String, Vector<DataObject>>(); //(HashMap<String, Vector<DataObject>>)Collections.synchronizedMap(new HashMap<String, Vector<DataObject>>());
		//network = new HashMap<String, String>(); //(HashMap<String, String>)Collections.synchronizedMap(new HashMap<String, String>());
		initIPList = new Vector<String>();
		network = new Vector<String>();

		for (int i=1; i<11; i++)
		{
			initIPList.add("192.168.0." + i);
			System.out.println("IP: " + initIPList.get(i-1));
		}	
	}	
	
	
	public synchronized String getInitIP()
	{		
		Random r = new Random();
		String ip = initIPList.get(r.nextInt(10) + 1);
		this.myIP = ip;
		return ip;
	}
	
	
	public synchronized static NetworkInfo getInstance() {
	      if(instance == null) {
	         instance = new NetworkInfo();
	      }
	      return instance;
	}	
	
	
	public synchronized String getMyIP() {
		return myIP;
	}


	public synchronized void setMyIP(String myIP) {
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

}
