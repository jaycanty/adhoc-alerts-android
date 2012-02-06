package com.aha.models;

import java.io.Serializable;

public class DataObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int messageType;
	private int destinationAddress;
	private int orginAddress;
	private int reassignAddress;
	private String message;

	
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

	public int getReassignAddress() {
		return reassignAddress;
	}

	public void setReassignAddress(int reassignAddress) {
		this.reassignAddress = reassignAddress;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

}
