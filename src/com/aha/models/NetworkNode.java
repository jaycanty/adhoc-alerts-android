package com.aha.models;

import java.io.Serializable;

public class NetworkNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int localRank;
	private int globalRank;
	private int ip;
	
	public int getLocalRank() {
		return localRank;
	}
	public void setLocalRank(int localRank) {
		this.localRank = localRank;
	}
	public int getGlobalRank() {
		return globalRank;
	}
	public void setGlobalRank(int globalRank) {
		this.globalRank = globalRank;
	}
	public int getIp() {
		return ip;
	}
	public void setIp(int ip) {
		this.ip = ip;
	}
}
