package com.aha.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;


public class NetworkInfo {
	
	private static NetworkInfo instance = null;
	private boolean networkUp;
	private boolean deviceInitiated;
	
	private String myIP;

	
	public HashMap<String, Vector<DataObject>> conversations;
	public HashMap<String, String> network;
	
	private Context alertsContext;
	private Handler alertsHandler;	
	//private boolean 
	
	protected NetworkInfo(){
		
		networkUp = false;
		deviceInitiated = false;
		conversations = new HashMap<String, Vector<DataObject>>(); //(HashMap<String, Vector<DataObject>>)Collections.synchronizedMap(new HashMap<String, Vector<DataObject>>());
		network = new HashMap<String, String>(); //(HashMap<String, String>)Collections.synchronizedMap(new HashMap<String, String>());
		
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


	public synchronized Context getAlertsContext() {
		return alertsContext;
	}

	public synchronized void setAlertsContext(Context alertsContext) {
		this.alertsContext = alertsContext;
		
		System.out.println("Context has been set");
		
		
		
	}

	public synchronized Handler getAlertsHandler() {
		return alertsHandler;
	}

	public synchronized void setAlertsHandler(Handler alertsHandler) {
		this.alertsHandler = alertsHandler;
		
		System.out.println("handler has been set: " + this.alertsHandler.toString());
		
		
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

}
