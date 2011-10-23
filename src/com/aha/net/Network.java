package com.aha.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class Network {
	
	DatagramSocket outSocket;
	ReceiveMessageThread rmt;
	
	public Network(){};
	
	public String sendMessage(String msg) {

	       byte[] data = new byte[512];

	       data = msg.getBytes();

	       try
	       {
	    	   System.out.println("in send message");
	    	   
	           outSocket = new DatagramSocket();
	           outSocket.setBroadcast(true);

	           DatagramPacket sendPacket;
	           sendPacket = new DatagramPacket(
	                           data, data.length, InetAddress.getByName("192.168.0.255"), 11111);

	           outSocket.send(sendPacket);

	           outSocket.close();

	       }
	       catch (Exception e)
	       {
	               e.printStackTrace();
	       }
	       //Toast.makeText(AdHoc.this, msg, Toast.LENGTH_SHORT).show();	
	       
	       System.out.println("message should be sent");
	       
			
			return "hello";
			
	}	
	

	public void startServer(Activity activity)
	{
		rmt = new ReceiveMessageThread();			
		rmt.start();
		
	}
	
	public void test() 
	{
		System.out.println("service connectewd!!");
		
	}
	
	private class ReceiveMessageThread extends Thread{

		  
		DatagramSocket datagramSocket;
		Handler handler;
		Activity activity;
			
		private ReceiveMessageThread() {
			
			
			System.out.println("Service has started!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");			
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
	    			byte[] buffer = new byte[512];  			 
	    			
	    			DatagramPacket brodcastReceivePacket = new DatagramPacket(buffer,buffer.length);
	    			
	    			datagramSocket.receive(brodcastReceivePacket);
	    			 
	    			String msg = new String(brodcastReceivePacket.getData());
	    			
	    			msg = msg.trim();
	    			
	    			System.out.println(msg);  // loose it   			
	    			
	    			handler.obtainMessage(3, -1, -1, msg).sendToTarget();
	    			
	    		}        	
	        	
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }		
		}//end run			 	
	
	
	}//end class
	
}
	
	
	

