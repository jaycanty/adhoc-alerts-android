package com.aha.models;

import java.io.Serializable;

public class DataObject implements Serializable {
	
	public static final int DISCOVERY = 0;
	public static final int BROADCAST = 1;
	public static final int ACK = 2;
	
	private static final long serialVersionUID = 1L;
	private int messageType;
	private String destinationAddress;
	private String orginAddress;
	private String message;

	
	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public String getOrginAddress() {
		return orginAddress;
	}

	public void setOrginAddress(String orginAddress) {
		this.orginAddress = orginAddress;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

}
