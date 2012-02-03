package com.aha.models;

import java.io.Serializable;

public class DataObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int messageType;
	private int destinationAddress;
	private int orginAddress;
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

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

}
