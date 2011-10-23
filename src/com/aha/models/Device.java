package com.aha.models;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;


import android.os.Build;

public class Device {
	
	HashMap<String, Integer> deviceMap = new HashMap<String, Integer>();
	public static final int DROID2 = 0;
	public static final int NEXUSONE = 1;
	public static final int ERIS = 2;	
		
	public Device() {		
		deviceMap.put("DROID2", new Integer(0));
		deviceMap.put("Nexus One", new Integer(1));
		deviceMap.put("Eris FroshedYo v11", new Integer(2));
	}
	
	public void connectDevice() {
		String model = Build.MODEL;		
		int device = (Integer)deviceMap.get(model).intValue();		
		System.out.println("" + device + " " + model);
		
        switch (device) {	    	        
	        case DROID2: 
	        	initDroid();
		        break; 
	        case NEXUSONE: 
	        	initNexus();
		        break; 
	        case ERIS: 
	        	initEris();
		        break;     
        }		
	}	
	
	public void disconnectDevice() {
		String model = Build.MODEL;		
		int device = (Integer)deviceMap.get(model).intValue();		
		System.out.println("" + device + " " + model);
		
        switch (device) {	    	        
	        case DROID2: 
	        	disconnectDroid();
		        break; 
	        case NEXUSONE: 
	        	disconnectNexus();
		        break; 
	        case ERIS: 
	        	disconnectEris();
		        break;     
        }		
	}	
	
	public void getStatus() {
		String model = Build.MODEL;		
		int device = (Integer)deviceMap.get(model).intValue();		
		System.out.println("" + device + " " + model);
		
        switch (device) {	    	        
        case DROID2: 
        	getStatusDroid();
	        break; 
        case NEXUSONE: 
        	disconnectNexus();
	        break; 
        case ERIS: 
        	disconnectEris();
	        break;     
    }		
		
	}
	
	public void initDroid() {		

		try {
			
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("sh /data/jay/connect.sh\n");
			out.writeBytes("exit\n");
			out.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	
	
	public void disconnectDroid() {

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("ifconfig tiwlan0 down\n");
			out.writeBytes("stop wpa_supplicant\n");
			out.writeBytes("rmmod tiwlan_drv\n");
			out.writeBytes("exit\n");
			out.flush();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	
	
	public void getStatusDroid() {

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			//out.writeBytes("ls\n");
			//out.writeBytes("cd /system/etc/wifi\n");
			out.writeBytes("wlan_cu -itiwlan0 -s /data/jay/stat.sh\n");
			out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line = null;
            System.out.println("<OUTPUT>");
            
            
            /*
            while ( (line = br.readLine()) != null)lsmod
                System.out.println(line);
                */
            
            for (int i=0; i<5; i++)
            {
            	line = br.readLine();
            	System.out.println(line);
            }
			
			br.close();
			out.writeBytes("exit\n");
			out.flush();
			out.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}

	public void initNexus() {		

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("insmod /system/lib/modules/bcm4329.ko\n");
			out.writeBytes("sleep 5\n");
			out.writeBytes("ifconfig eth0 192.168.0.4 netmask 255.255.255.0\n");
			out.writeBytes("ifconfig eth0 up\n");
			out.writeBytes("./data/data/android.tether/bin/iwconfig eth0 mode ad-hoc\n");
			out.writeBytes("./data/data/android.tether/bin/iwconfig eth0 essid hope\n");
			out.writeBytes("exit\n");
			out.flush();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	
	
	public void disconnectNexus() {

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("ifconfig eth0 down\n");
			out.writeBytes("rmmod bcm4329\n");
			out.writeBytes("exit\n");
			out.flush();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	
	
	public void initEris() {		

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("insmod /system/lib/modules/wlan.ko\n");
			out.writeBytes("sleep 5\n");
			out.writeBytes("wlan_loader -f /system/etc/wifi/Fw1251r1c.bin -e /proc/calibration -i /system/etc/wifi/tiwlan.ini\n");
			out.writeBytes("sleep 2\n");
			out.writeBytes("ifconfig tiwlan0 192.168.0.5 netmask 255.255.255.0 up\n");
			out.writeBytes("exit\n");
			out.flush();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	

	public void disconnectEris() {

		try {
			Process ps = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(ps.getOutputStream());
			out.writeBytes("ifconfig tiwlan0 down\n");
			out.writeBytes("rmmod wlan\n");
			out.writeBytes("exit\n");
			out.flush();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} 
	}	
	
	
}
