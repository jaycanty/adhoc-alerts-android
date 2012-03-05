package com.aha.models;

import java.io.Serializable;

public class NetworkNode implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;
	
	private long localRank;
	private int ip;
	private boolean hasNew;
	
	
	public NetworkNode(long localRank, int ip)
	{
		this.localRank = localRank;
		this.ip = ip;
		this.hasNew = false;
	}
	
	public NetworkNode(NetworkNode nn)
	{
		this.localRank = nn.getLocalRank();
		this.ip = nn.getIp();
		this.hasNew = false;
	}
	
	public synchronized long getLocalRank() {
		return localRank;
	}
	public synchronized void setLocalRank(long localRank) {
		this.localRank = localRank;
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
