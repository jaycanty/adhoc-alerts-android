package com.aha.models;

import java.io.Serializable;

public class NetworkNode implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;
	
	private int localRank;
	private int globalRank;
	private int ip;
	private boolean hasNew;
	
	
	public NetworkNode(int localRank, int globalRank, int ip)
	{
		this.localRank = localRank;
		this.globalRank = globalRank;
		this.ip = ip;
		this.hasNew = false;
	}
	
	public synchronized int getLocalRank() {
		return localRank;
	}
	public synchronized void setLocalRank(int localRank) {
		this.localRank = localRank;
	}
	public synchronized int getGlobalRank() {
		return globalRank;
	}
	public synchronized void setGlobalRank(int globalRank) {
		this.globalRank = globalRank;
	}
	public synchronized int getIp() {
		return ip;
	}
	public synchronized void setIp(int ip) {
		this.ip = ip;
	}
	public synchronized boolean hasNew() {
		return hasNew;
	}

	public synchronized void setHasNew(boolean hasNew) {
		this.hasNew = hasNew;
	}

	public synchronized int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		int otherIP = ((NetworkNode)arg0).getIp();
		
		if (ip == Constants.BROADCAST)
			return -1;
		
		if (otherIP == Constants.BROADCAST)
			return 1;
		
		if (ip > otherIP)
			return 1;
		else if (ip < otherIP)
			return -1;
		
		return 0;
	}
}
