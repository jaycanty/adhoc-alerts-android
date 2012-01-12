package com.aha.models;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;

public class AppInfo {

	
	private static AppInfo instance = null;
	
	private Context networkContext = null;
	private Handler networkHandler = null;		
	private Context alertsContext = null;
	private Handler alertsHandler = null;	
	private Context conversationContext = null;
	private Handler conversationHandler = null;	
	
	protected AppInfo(){}	
	
	public synchronized static AppInfo getInstance() {
	      if(instance == null) {
	         instance = new AppInfo();
	      }
	      return instance;
	}		
	
	
	public Context getNetworkContext() {
		return networkContext;
	}

	public void setNetworkContext(Context networkContext) {
		this.networkContext = networkContext;
	}

	public Handler getNetworkHandler() {
		return networkHandler;
	}

	public void setNetworkHandler(Handler networkHandler) {
		this.networkHandler = networkHandler;
	}

	public synchronized Context getAlertsContext() {
		return alertsContext;
	}

	public synchronized void setAlertsContext(Context alertsContext) {
		this.alertsContext = alertsContext;
		
		System.out.println("Context has been set");
		
		
		
	}

	public synchronized Handler getAlertsHandler() {
		return alertsHandler;
	}

	public synchronized void setAlertsHandler(Handler alertsHandler) {
		this.alertsHandler = alertsHandler;
		
		System.out.println("handler has been set: " + this.alertsHandler.toString());
		
		
	}

	public Context getConversationContext() {
		return conversationContext;
	}

	public void setConversationContext(Context conversationContext) {
		this.conversationContext = conversationContext;
	}

	public Handler getConversationHandler() {
		return conversationHandler;
	}

	public void setConversationHandler(Handler conversationHandler) {
		this.conversationHandler = conversationHandler;
	}		
	
	
}
