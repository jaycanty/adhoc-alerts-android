package com.aha.models;

import java.io.Serializable;

public class NetworkNode implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;
	
	private int localRank;
	private int globalRank;
	private int ip;
	
	public NetworkNode(int localRank, int globalRank, int ip)
	{
		this.localRank = localRank;
		this.globalRank = globalRank;
		this.ip = ip;
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
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		int otherIP = ((Integer)arg0).intValue();
		
		if (ip > otherIP)
			return 1;
		else if (ip < otherIP)
			return -1;
		
		return 0;
	}
}
