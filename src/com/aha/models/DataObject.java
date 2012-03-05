package com.aha.models;

import java.io.Serializable;
import java.util.Vector;

public class DataObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int messageType;
	private int destinationAddress;
	private int orginAddress;
	private int auxillaryAddress;
	private int localRank;
	private int globalRank;
	private String message;
	
	private Object object1;
	private Object object2;
	
	public DataObject(){}
	
	public DataObject(DataObject dataO){
		
		this.messageType = dataO.getMessageType();
		this.destinationAddress = dataO.getDestinationAddress();
		this.orginAddress = dataO.getOrginAddress();
		this.auxillaryAddress = dataO.getOrginAddress();
		this.localRank = dataO.getLocalRank();
		this.globalRank = dataO.getGlobalRank();
		this.message = dataO.getMessage();
		
		this.object1 = null; //dataO.getObject1();
		this.object2 = null; //dataO.getObject2();		
		
	}
	
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
	
	public int getAuxillaryAddress() {
		return auxillaryAddress;
	}

	public void setAuxillaryAddress(int auxillaryAddress) {
		this.auxillaryAddress = auxillaryAddress;
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
