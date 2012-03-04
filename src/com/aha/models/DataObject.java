package com.aha.models;

import java.io.Serializable;
import java.util.Vector;

public class DataObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int messageType;
	private int destinationAddress;
	private int orginAddress;
	private int joinAddress;
	private int reassignAddress;
	private int localRank;
	private int globalRank;
	private String message;
	
	private Object object1;
	private Object object2;
	
	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(int destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public int getOrginAddress() {
		return orginAddress;
	}

	public void setOrginAddress(int orginAddress) {
		this.orginAddress = orginAddress;
	}
	
	public int getJoinAddress() {
		return joinAddress;
	}

	public void setJoinAddress(int joinAddress) {
		this.joinAddress = joinAddress;
	}	

	public int getReassignAddress() {
		return reassignAddress;
	}

	public void setReassignAddress(int reassignAddress) {
		this.reassignAddress = reassignAddress;
	}

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

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject1() {
		return object1;
	}

	public void setObject1(Object object1) {
		this.object1 = object1;
	}

	public Object getObject2() {
		return object2;
	}

	public void setObject2(Object object2) {
		this.object2 = object2;
	}

	

}
