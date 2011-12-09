package com.aha.models;


public class NetworkInfo {
	
	private static NetworkInfo instance = null;
	private boolean networkUp;
	private boolean deviceInitiated = false;
	//private boolean 
	
	protected NetworkInfo(){
		
		networkUp = false;
		deviceInitiated = false;
		
	}
	
	public synchronized static NetworkInfo getInstance() {
	      if(instance == null) {
	         instance = new NetworkInfo();
	      }
	      return instance;
	}	


	public boolean isNetworkUp() {
		return networkUp;
	}

	public void setNetworkUp(boolean networkUp) {
		this.networkUp = networkUp;
	}

	public synchronized boolean isDeviceInitiated() {
		return deviceInitiated;
	}

	public synchronized void setDeviceInitiated(boolean deviceInitiated) {
		this.deviceInitiated = deviceInitiated;
	}	

}
