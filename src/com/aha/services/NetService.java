package com.aha.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.aha.net.Network; 
import com.aha.models.DataObject;
import com.aha.models.Device;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

public class NetService extends Service{
	
	private static WifiManager wfMan;
	private final IBinder mBinder = new LocalBinder();
	private final Device device = new Device();
	private Network network;
	private DatagramSocket outSocket;
	private ReceiveMessageThread rmt;
	
	  @Override
	  public void onCreate() {
	
		  wfMan = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		  network = new Network();
	  }	
	
	
	
	public class LocalBinder extends Binder {
	    public NetService getService() {
	        // Return this instance of LocalService so clients can call public methods
	        return NetService.this;
	    }
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	    return mBinder;
	}
	
	    /** method for clients */
	public String getConnectionInfo() {
	    					
		WifiInfo wfInfo = wfMan.getConnectionInfo();
				
		return wfInfo.toString();		
		
	}

		
	public String enableWifi() {
	    			
		wfMan.setWifiEnabled(true); 
		
		return "Wifi Enabled";					
	}
	
	public String disableWifi() {
		
		wfMan.setWifiEnabled(false);    	
		return "Wifi Disabled";					
	}	
	
	public String advertiseNet() {		

		device.connectDevice();
			
		return "Advertising Network";
	}
	
	
	public String netOff() {

		device.disconnectDevice();
			
		//device.getStatus();
		
		return "Network Off";
	}	
	
	public String sendMessage(String msg) {

		
		/*
	       byte[] data = new byte[512];

	       data = msg.getBytes();
	      */ 

			DataObject dataObject = new DataObject();
			dataObject.setMessage(msg);			
			
	       
	       try
	       {	
	    	   
	    	   
	           
	    	   
	    	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	           ObjectOutputStream oos = new ObjectOutputStream(baos);
	           oos.writeObject(dataObject);
	           oos.flush();	    
	           
	           byte[] Buf= baos.toByteArray();
	           
	           System.out.println(Buf.length);
	           
	           outSocket = new DatagramSocket();
	           outSocket.setBroadcast(true);

	           DatagramPacket sendPacket;
	           sendPacket = new DatagramPacket(
	                           Buf, Buf.length, InetAddress.getByName("192.168.0.255"), 11111);

	           outSocket.send(sendPacket);

	           outSocket.close();
	       }
	       catch (Exception e)
	       {
	               e.printStackTrace();
	       }
	       //Toast.makeText(AdHoc.this, msg, Toast.LENGTH_SHORT).show();
	       
	       System.out.println("message should be sent " + msg);
			
			return "hello";
			
	}	

	public void startDaemon(Context context, Handler handler)
	{
		
		
		rmt = new ReceiveMessageThread(context, handler);			
		rmt.start();
		
	}
	
	private class ReceiveMessageThread extends Thread{

		  
		DatagramSocket datagramSocket;
		Handler handler;
			
		public ReceiveMessageThread(Context context, Handler handler) {
			
			System.out.println("Service has started!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
			this.handler = handler;		
		}			  
		
		public void run() 
		{	
	        try
	        {
	        	datagramSocket = new DatagramSocket(11111);
	        	datagramSocket.setBroadcast(true);
	        	        	
	    		while(true) 
	    		{ 

	    			// 52kb buffer
	    			byte[] buffer = new byte[82];  			 
	    			
	    			DatagramPacket brodcastReceivePacket = new DatagramPacket(buffer,buffer.length);
	    			
	    			datagramSocket.receive(brodcastReceivePacket);
	    			 
	    			//String msg = new String(brodcastReceivePacket.getData());
	    			
	    			//msg = msg.trim();
	    			
	    			
	    	        ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
	    	        ObjectInputStream oos = new ObjectInputStream(baos);
	    	        DataObject inObject = (DataObject)oos.readObject();	    			
	    			
	    			
	    			
	    			System.out.println(inObject.getMessage());  // loose it   	
	    			
	    			handler.obtainMessage(3, -1, -1, inObject.getMessage()).sendToTarget();
	    			
	    		}        	
	        	
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }		
		}//end run			 	
	
	
	}//end inner class
	
	public void test() 
	{
		System.out.println("service connectewd!!");
		
	}
	
}
