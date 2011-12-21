package com.aha.models;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;

public class AppInfo {

	private static AppInfo instance = null;
	private Context alertsContext = null;
	private Handler alertsHandler = null;	
	
	protected AppInfo(){}	
	
	public synchronized static AppInfo getInstance() {
	      if(instance == null) {
	         instance = new AppInfo();
	      }
	      return instance;
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
	
	
}
