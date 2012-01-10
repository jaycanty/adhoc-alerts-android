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
import java.util.Arrays;
import java.util.Vector;

import com.aha.models.AppInfo;
import com.aha.models.Constants;
import com.aha.models.DataObject;
import com.aha.models.NetworkInfo;

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
import android.net.wifi.WifiInfo;

public class NetService extends Service{
		
	private static WifiManager wfMan;
	private final IBinder mBinder = new LocalBinder();
	private final Device device = new Device();
	private DatagramSocket outSocket;
	private ReceiveMessageThread rmt;
	private NetworkThread nt;

	@Override
	  public void onCreate() {
	
		  wfMan = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
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
	
	public String advertiseNet(int lRank, Context context, Handler handler) {		

		device.connectDevice();
			
		return "Advertising Network";
	}
	
	
	public String netOff() {

		device.disconnectDevice();
			
		//device.getStatus();
		
		return "Network Off";
	}	
	
	public String getStatus() {

		if(device.doesNetworkExist())
			return "Network is up";
		else
			return "Network off";
			
	}	
	
	
	public String sendMessage(DataObject dataObject) {
	       
	       try
	       {	
	    	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	           ObjectOutputStream oos = new ObjectOutputStream(baos);
	           oos.writeObject(dataObject);
	           oos.flush();	    
	           
	           byte[] buf= baos.toByteArray();
	
	           System.out.println(buf.length);
	           
	           int l = buf.length;
	           
	           byte[] len = new byte[4];
	           
	           for (int i = 0; i < 4; ++i) {
	               int shift = i << 3; // i * 8
	               len[3-i] = (byte)((l & (0xff << shift)) >>> shift);
	           }
	           
	           byte[] packet = new byte[512];

	           System.arraycopy(len, 0, packet, 0, len.length);
	           System.arraycopy(buf, 0, packet, len.length, buf.length);	           
	           
	           outSocket = new DatagramSocket();
	           outSocket.setBroadcast(true);

	           DatagramPacket sendPacket;
	           sendPacket = new DatagramPacket(
	        		   packet, packet.length, InetAddress.getByName(dataObject.getDestinationAddress()), 11111);

	           outSocket.send(sendPacket);

	           outSocket.close();
	       }
	       catch (Exception e)
	       {
	               e.printStackTrace();
	       }
	       //Toast.makeText(AdHoc.this, msg, Toast.LENGTH_SHORT).show();
	       
	       System.out.println("message should be sent " + dataObject.getMessage());
			
			return "hello";
			
	}	

	public void startDaemon()
	{
		try {
			rmt = new ReceiveMessageThread();			
			rmt.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private class ReceiveMessageThread extends Thread{

		  
		DatagramSocket datagramSocket;
		//Handler handler;
			
		public ReceiveMessageThread() {
			
			System.out.println("alerts daemon has started!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//this.handler = handler;
			
			
		}			  
		
		public void run() 
		{	
	        try
	        {
	        	datagramSocket = new DatagramSocket(11111);
	        	datagramSocket.setBroadcast(true);
	        	        	
	    		while(true) 
	    		{ 
	    			try {
		    			// 52kb buffer
		    			byte[] buffer = new byte[512];  			 
		    			
		    			DatagramPacket brodcastReceivePacket = new DatagramPacket(buffer,buffer.length);
		    			
		    			datagramSocket.receive(brodcastReceivePacket);
		    			 
		    		      int len = 0;
		    		      // byte[] -> int
		    		      for (int i = 0; i < 4; ++i) {
		    		          len |= (buffer[3-i] & 0xff) << (i << 3);
		    		      }
		    				
		    		    System.out.println("Length = " + len);
		    		      
		    			byte[] packet = new byte[len];
		    				 
		    			System.arraycopy(buffer, 4, packet, 0, len);
		    			
		    	        ByteArrayInputStream baos = new ByteArrayInputStream(packet);
		    	        ObjectInputStream oos = new ObjectInputStream(baos);
		    	        DataObject inObject = (DataObject)oos.readObject();	    			
	
		    			System.out.println(inObject.getMessage());  // loose it 
		    			
		    			String orginIP = brodcastReceivePacket.getAddress().getHostAddress();		    			
		    			System.out.println(orginIP);
		    			
		    			NetworkInfo ni = NetworkInfo.getInstance();
		    			
		    			if(!inObject.getOrginAddress().equalsIgnoreCase(ni.getMyIP()))
		    			{
			    		
			    			if (ni.conversations.containsKey(orginIP)) {
			    				Vector<DataObject> v = ni.conversations.get(orginIP);
			    				v.add(inObject);
			    			} else {
				    			Vector<DataObject> v = new Vector<DataObject>();
				    			v.add(inObject);
				    			ni.conversations.put(brodcastReceivePacket.getAddress().getHostAddress(), v);	
			    			}			
			    			
			    			
			    			
			    			Handler alertsHandler = AppInfo.getInstance().getAlertsHandler();
			    			Handler convoHandler = AppInfo.getInstance().getConversationHandler();
			    			
			    			if (alertsHandler != null)
			    				alertsHandler.obtainMessage(3, -1, -1, "").sendToTarget();
			    			else
			    				System.out.println("The activity hasn't strated: " + inObject.getMessage());
			    			
			    			if (convoHandler != null)
			    				convoHandler.obtainMessage(3, -1, -1, "").sendToTarget();		    			
		    			}
		    			
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    			
	    			
	    		}        	
	        	
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }		
		}//end run			 	
	
	
	}//end inner class
	

	public void startNetwork (int lRank, Context context, Handler handler)
	{
		
		nt = new NetworkThread(context, handler);			
		nt.start();
		
	}	
	
	
	private class NetworkThread extends Thread	{

		Context context;
		Handler handler;
		NetworkInfo ni;
			
		public NetworkThread(Context context, Handler handler) {
			
			System.out.println("Service has started!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
			this.handler = handler;
			this.context = context;
			ni = NetworkInfo.getInstance();
			
		}			  
		
		public void run() 
		{	
	        try
	        {
	        	System.out.println("In thread");
	        	
	    		if(!ni.isNetworkUp()) 
	    		{ 
	    			System.out.println("Network is down");
	    			
	    			if (!ni.isDeviceInitiated())
	    			{
		    			device.connectDevice();
		    			ni.setDeviceInitiated(true);
		    			
		    			
		    			System.out.println("Device is initialized");
		    			
		    			
	    			}
	    			
	    			
	    			System.out.println("Sleep started");
	    			NetworkThread.sleep(5000); //(80000);
	    			System.out.println("Sleep ended");
	    			
	    			if(!device.doesNetworkExist())
	    			{
	    				System.out.println("A network does not exist");
	    				device.disconnectDevice();
	    				handler.obtainMessage(3, -1, -1, "There is no network available at this time").sendToTarget();
	    			}
	    			else	
	    			{	
	    				System.out.println("A network does exist");
	    				ni.setNetworkUp(device.doesNetworkExist());
	    				
	    				handler.obtainMessage(3, -1, -1, "The network is up").sendToTarget();
	    				
	    				startDaemon();
	    				
	    			}	
/*	    			
	    			DataObject dataObject = new DataObject();
	    			dataObject.setDestinationAddress(Constants.BROADCAST);
	    			dataObject.setOrginAddress(ni.getMyIP());
	    			dataObject.setMessageType(Constants.JOIN);
	*/    			
	    			
	    			
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
