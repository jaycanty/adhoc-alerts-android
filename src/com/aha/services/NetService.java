package com.aha.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import com.aha.R;
import com.aha.activities.InfoActivity;
import com.aha.activities.NetworkActivity;
import com.aha.models.AppInfo;
import com.aha.models.Constants;
import com.aha.models.DataObject;
import com.aha.models.NetworkInfo;
import com.aha.models.NetworkNode;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.net.wifi.WifiInfo;

public class NetService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private final Device device = new Device();
	private DatagramSocket outSocket;
	private ReceiveMessageThread rmt;
	private NetworkThread nt;
	public static boolean quit = false;

	@Override
	public void onCreate() {
				
		// write scripts to sdcard
		String[] scriptArray = {"connect", "cli"}; 		
		for (int i=0; i<scriptArray.length; i++)
		{
			try {
			    File root = Environment.getExternalStorageDirectory();
			    if (root.canWrite()){
			    				    	
			    	InputStream is = null;
			    	if (i == 0)
			    		is = getResources().openRawResource(R.raw.connect);
			    	else
			    		is = getResources().openRawResource(R.raw.cli);
			        BufferedReader in = new BufferedReader(new InputStreamReader(is));
			       			        
			        File scriptFile = new File(root, scriptArray[i] + ".sh");		        
			        FileWriter gpxwriter = new FileWriter(scriptFile);
			        BufferedWriter out = new BufferedWriter(gpxwriter);
			        
					String sCurrentLine;
					 	 
					while ((sCurrentLine = in.readLine()) != null) 	
						out.write(sCurrentLine + "\n");
		
			        in.close();
			        out.close();
			    }
			} catch (IOException e) {
			    System.out.println("Could not write file " + e.getMessage());
			}				
		}
	}

	public class LocalBinder extends Binder {
		public NetService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return NetService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public String advertiseNet(int lRank, Context context, Handler handler) 
	{
		device.connectDevice();
		return "Advertising Network";
	}

	public String netOff() {
		
		NetworkInfo ni = NetworkInfo.getInstance();
		NetService.quit = true;
		
		if (ni.getMyIP() == Constants.HUB_IP)
		{
			System.out.println("Got to here");
			
			NetworkNode maxNN = null;
			long max = 0;
			
			for (int i=0; i<ni.network.size(); i++)
			{
				NetworkNode nn = ni.network.get(i);
				
				if (nn.getLocalRank() > max)
				{
					max = nn.getLocalRank();
					maxNN = nn;					
				}
			}
			
			DataObject dataObject = new DataObject();
			dataObject.setDestinationAddress(maxNN.getIp());
			dataObject.setOrginAddress(ni.getMyIP());
			dataObject.setMessageType(Constants.HUB);
			sendMessage(dataObject);			
			
		} 
	
		DataObject dataObject = new DataObject();
		dataObject.setDestinationAddress(Constants.BROADCAST);
		dataObject.setOrginAddress(ni.getMyIP());
		dataObject.setMessageType(Constants.QUIT);
		sendMessage(dataObject);

		device.disconnectDevice();
		ni.setDeviceInitiated(false);
		ni.setNetworkUp(false);
		ni.setJoined(false);
		ni.setAcknowledged(false);
		ni.network.clear();
		ni.conversations.clear();
	
		Handler netHandler = AppInfo.getInstance().getAlertsHandler();
		Handler convoHandler = AppInfo.getInstance().getConversationHandler();		
		Handler infoHandler = AppInfo.getInstance().getNetworkHandler();

		int ip = ni.getInitIP();
		
		if (infoHandler != null)
		{
			infoHandler.obtainMessage(2, -1, -1, Constants.BASE_ADDRESS + ip).sendToTarget();
			infoHandler.obtainMessage(3, 5, -1,
					"Network is off\nRe-'connect' anytime.")
					.sendToTarget();
		}
		if (netHandler != null)
			netHandler.obtainMessage(3, -1, -1, "")
					.sendToTarget();
		
		if (convoHandler != null)
			convoHandler.obtainMessage(3, -1, -1, "")
					.sendToTarget();

		// device.getStatus();
		
		return "Network Off";
	}

	public String getStatus() {

		if (device.doesNetworkExist())
			return "Network is up";
		else
			return "Network off";

	}

	public synchronized void sendMessage(DataObject dataObject) {

		try {
			
			System.out.println("OIP: " + dataObject.getOrginAddress());
			System.out.println("DIP: " + dataObject.getDestinationAddress());			
			
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(dataObject);
			oos.flush();

			byte[] buf = baos.toByteArray();

			int l = buf.length;

			byte[] len = new byte[4];

			for (int i = 0; i < 4; ++i) {
				int shift = i << 3; // i * 8
				len[3 - i] = (byte) ((l & (0xff << shift)) >>> shift);
			}

			byte[] packet = new byte[4096];

			System.arraycopy(len, 0, packet, 0, len.length);
			System.arraycopy(buf, 0, packet, len.length, buf.length);

			outSocket = new DatagramSocket();
			outSocket.setBroadcast(true);

			DatagramPacket sendPacket;
			sendPacket = new DatagramPacket(packet, packet.length,
					InetAddress.getByName( Constants.BASE_ADDRESS + dataObject.getDestinationAddress()),
					11111);

			outSocket.send(sendPacket);

			outSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Toast.makeText(AdHoc.this, msg, Toast.LENGTH_SHORT).show();
		//return "hello";
	}

	public void startDaemon() {
		try {
			rmt = new ReceiveMessageThread();
			rmt.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ReceiveMessageThread extends Thread {

		DatagramSocket datagramSocket;
		ObjectInputStream oos;

		public ReceiveMessageThread() {}

		public void run() {
			try {
				datagramSocket = new DatagramSocket(11111);
				datagramSocket.setBroadcast(true);

				while (NetworkInfo.getInstance().isNetworkUp()) {
					try {
						// 52kb buffer
						byte[] buffer = new byte[4096];

						DatagramPacket brodcastReceivePacket = new DatagramPacket(
								buffer, buffer.length);

						datagramSocket.receive(brodcastReceivePacket);

						int len = 0;
						// byte[] -> int
						for (int i = 0; i < 4; ++i) {
							len |= (buffer[3 - i] & 0xff) << (i << 3);
						}

						System.out.println("Length = " + len);

						byte[] packet = new byte[len];
						System.arraycopy(buffer, 4, packet, 0, len);
						ByteArrayInputStream baos = new ByteArrayInputStream(
								packet);
						oos = new ObjectInputStream(baos);
						DataObject inObject = (DataObject) oos.readObject();

						System.out.println("YOU HAVE A MESSAGE");
						System.out.println("OIP: " + inObject.getOrginAddress());
						System.out.println("DIP: " + inObject.getDestinationAddress());
						System.out.println("TYPE: " + inObject.getMessageType());
						
						handleMessage(inObject);
						
						if (inObject.getMessageType() == Constants.QUIT)
						{
							NetworkInfo ni = NetworkInfo.getInstance();
							
							if (inObject.getOrginAddress() == ni.getMyIP() && NetService.quit)
							{
								System.out.println("QUIT");
								ni.setNetworkUp(false);
								NetService.quit = false;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
				oos.close();
				datagramSocket.close();
				System.out.println("GRACEFULL");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}// end run
	}// end inner class

	public void handleMessage(DataObject dataObject) {

		MessageHandler mh = new MessageHandler(dataObject);
		mh.start();

	}

	private class MessageHandler extends Thread {

		DataObject inObject;
		Handler netHandler = AppInfo.getInstance().getAlertsHandler();
		Handler infoHandler = AppInfo.getInstance().getNetworkHandler();
		Handler convoHandler = AppInfo.getInstance().getConversationHandler();
		Vector<DataObject> v;
		
		public MessageHandler(DataObject dataObject) {

			this.inObject = dataObject;
			//ni = NetworkInfo.getInstance();
			v = new Vector<DataObject>();

		}

		public void run() {
			try {
				int orginIP = inObject.getOrginAddress();

				NetworkInfo ni = NetworkInfo.getInstance();

				if (inObject.getOrginAddress() != ni.getMyIP()) {
					
					switch (inObject.getMessageType()) {
					case Constants.JOIN:
						
						if (ni.getMyIP() == Constants.HUB_IP)
						{
							int highIP = 0;
							
							if (ni.network.size() == 0 || ni.network.size() == 1 || ni.network == null) {
								highIP = 13;
								infoHandler.obtainMessage(3, 4, -1,
								"YEPEE!\nAnother has joined the network, you can send and receive alerts.")
								.sendToTarget();
							}
							else {
								Collections.sort(ni.network);
								highIP = ni.network.get(ni.network.size()-1).getIp() + 1;
							}
							
							System.out.println(highIP);
							
							//highIP notify others
							DataObject bcastObject = new DataObject();
							bcastObject.setDestinationAddress(Constants.BROADCAST);
							bcastObject.setOrginAddress(ni.getMyIP());
							bcastObject.setLocalRank(ni.getLocalRank());
							bcastObject.setAuxillaryAddress(highIP);
							bcastObject.setMessageType(Constants.NEW_MEMBER);
							bcastObject.setMessage("" + highIP + " welcome to the network!");	
							sendMessage(bcastObject);
							
							DataObject outObject = new DataObject();
							outObject.setDestinationAddress(inObject
									.getOrginAddress());
							outObject.setOrginAddress(ni.getMyIP());
							outObject.setMessageType(Constants.JOIN_ACK);
							outObject.setAuxillaryAddress(highIP);
							outObject.setMessage("" + highIP + " welcome to the network!");
							
							if (ni.conversations.containsKey(Constants.BROADCAST)) {
								v = ni.conversations
										.get(Constants.BROADCAST);
								v.add(new DataObject(outObject));
							} else {
								v = new Vector<DataObject>();
								v.add(new DataObject(outObject));
								ni.conversations.put(Constants.BROADCAST, v);
							}								
							//load network
							Vector<DataObject> conVec = new Vector<DataObject>(ni.conversations.get(Constants.BROADCAST));
							Vector<NetworkNode> netVec =  new Vector<NetworkNode>(ni.network);
							// add me
							netVec.add(new NetworkNode(ni.getLocalRank(),ni.getMyIP()));
							
							outObject.setObject1(netVec);
							outObject.setObject2(conVec);
		
							sendMessage(outObject);
							
							ni.network.add(new NetworkNode(inObject.getLocalRank(),highIP));
							
							if (netHandler != null)
								netHandler.obtainMessage(2, -1, -1, "").sendToTarget(); 	

						}
						break;
					case Constants.JOIN_ACK:
						
						if (ni.getMyIP() == inObject.getDestinationAddress())
						{
							System.out.println("THE JOIN HAS BEEN ACKED MYIP BY: " + inObject.getOrginAddress());
							
							ni.network.clear();
							ni.conversations.clear();
							
							Vector<NetworkNode> netVec =  (Vector<NetworkNode>)inObject.getObject1();
							Vector<DataObject> conVec = (Vector<DataObject>)inObject.getObject2();
							
							ni.conversations.put(Constants.BROADCAST, conVec);

							// load nn's
							for (int i=0; i<netVec.size(); i++)
							{
								NetworkNode nn = new NetworkNode(netVec.get(i));
								if (nn.getIp() == Constants.BROADCAST)
									nn.setHasNew(true);
								ni.network.add(nn);
								System.out.println("NET NODE: " + netVec.get(i).getLocalRank());
							}	
							Collections.sort(ni.network);
							
							ni.setAcknowledged(true);
							
							int ip = 0;

							ip = inObject.getAuxillaryAddress();
							ni.setMyIP(ip);
							device.changeIP(ip);	
							
							if (infoHandler != null)
							{
								infoHandler.obtainMessage(2, -1, -1, Constants.BASE_ADDRESS + ip).sendToTarget(); 
								infoHandler.obtainMessage(3, 4, -1,
								"YEPEE!\nA network exists, you can send and receive alerts.")
								.sendToTarget();
							}
							if (netHandler != null)
								netHandler.obtainMessage(2, -1, -1, "").sendToTarget();								

						}				
						
						break;
					case Constants.ALERT:
						
						if (inObject.getDestinationAddress() == Constants.BROADCAST)
						{
							if (ni.conversations.containsKey(Constants.BROADCAST)) {
								v = ni.conversations
										.get(Constants.BROADCAST);
								v.add(inObject);
							} else {
								v = new Vector<DataObject>();
								v.add(inObject);
								ni.conversations.put(Constants.BROADCAST, v);
							}
							if (NetworkActivity.inFocus || InfoActivity.inFocus)
								ni.getNetworkNode(Constants.BROADCAST).setHasNew(true);							
													
						} else {
							
							if (ni.conversations.containsKey(orginIP)) {
								v = ni.conversations
										.get(orginIP);
								v.add(inObject);
							} else {
								v = new Vector<DataObject>();
								v.add(inObject);
								ni.conversations.put(orginIP, v);
							}
							if (NetworkActivity.inFocus || InfoActivity.inFocus)
								ni.getNetworkNode(orginIP).setHasNew(true);
						}

						if (netHandler != null)
							netHandler.obtainMessage(3, -1, -1, "")
									.sendToTarget();

						if (convoHandler != null)
							convoHandler.obtainMessage(3, -1, -1, "")
									.sendToTarget();
						break;
					case Constants.QUIT:
						
						if (inObject.getOrginAddress() != Constants.HUB_IP)
						{
							int index = 0;
							
							for (int i=0; i<ni.network.size(); i++)
								if (ni.network.get(i).getIp() == inObject.getOrginAddress())
									index = i;
							
							if (index > 0)
							{
								ni.conversations.remove(inObject.getOrginAddress());
								ni.network.remove(index);	
							}
													
							if (netHandler != null)
								netHandler.obtainMessage(2, -1, -1, "").sendToTarget();	
							
							if (ni.network.size() < 2)
							{
								if (device.deviceCanAdvertiseNetwork())
								{								
									infoHandler.obtainMessage(3, 6, -1,
									"There are no other devices available, you are advertising the network")
									.sendToTarget();
																	
								} else {
									
									infoHandler.obtainMessage(3, 6, -1,
									"There are no other devices available, you are advertising the network for 1 minute")
									.sendToTarget();
																	
									NetworkThread.sleep(50000);
									
									netOff();
								}
							}
						}
						break;
					case Constants.NEW_MEMBER:
						
						if (ni.getMyIP() > 11)
						{
							ni.network.add(new NetworkNode(inObject.getLocalRank(), inObject.getAuxillaryAddress()));
							
							if (netHandler != null)
								netHandler.obtainMessage(2, -1, -1, "").sendToTarget();								
						}				
						break;
					case Constants.HUB:
												
						DataObject dataObject = new DataObject();
						dataObject.setDestinationAddress(Constants.BROADCAST);
						dataObject.setOrginAddress(ni.getMyIP());
						dataObject.setMessageType(Constants.QUIT);
						sendMessage(dataObject);

						ni.setMyIP(Constants.HUB_IP);
						device.changeIP(Constants.HUB_IP);

						int index = 0;
						
						for (int i=0; i<ni.network.size(); i++)
							if (ni.network.get(i).getIp() == Constants.HUB_IP)
								index = i;
						
						if (index > 0)
						{
							ni.conversations.remove(Constants.HUB_IP);
							ni.network.remove(index);
						}
						
						if (infoHandler != null)
						{
							infoHandler.obtainMessage(2, -1, -1, Constants.BASE_ADDRESS + Constants.HUB_IP).sendToTarget(); 
							infoHandler.obtainMessage(3, 4, -1,
							"You are the new hub.")
							.sendToTarget();
						}
						if (netHandler != null)
							netHandler.obtainMessage(2, -1, -1, "").sendToTarget();			
						
						if (ni.network.size() < 2)
						{
							if (device.deviceCanAdvertiseNetwork())
							{								
								infoHandler.obtainMessage(3, 6, -1,
								"There are no other devices available, you are advertising the network")
								.sendToTarget();
																
							} else {
								
								infoHandler.obtainMessage(3, 6, -1,
								"There are no other devices available, you are advertising the network for 1 minute")
								.sendToTarget();
																
								NetworkThread.sleep(50000);
								
								netOff();
							}
						}						
						
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}// end run
	}// end inner class

	public void startNetwork(int lRank) {
		
		//"30minutes", "1hour", "2hours", "3hours"
		
		System.out.println("INDEX: " + lRank);
		
		long time = System.currentTimeMillis();
		
		switch (lRank) {
		case 0:
			time += 30 * 60 * 1000;
			break;
		case 1:
			time += 60 * 60 * 1000;
			break;		
		case 2:
			time += 120 * 60 * 1000;
			break;
		case 3:
			time += 360 * 60 * 1000;
			break;
		}
		NetworkInfo.getInstance().setLocalRank(time);
		
		nt = new NetworkThread();
		nt.start();

	}

	private class NetworkThread extends Thread {

		NetworkInfo ni;

		public NetworkThread() {

			ni = NetworkInfo.getInstance();

		}

		
		public void run() {
			Handler handler = AppInfo.getInstance().getNetworkHandler();
			try {
						
				if (!ni.isNetworkUp()) {
										
					if (!ni.isDeviceInitiated()) {
						device.connectDevice();
						ni.setDeviceInitiated(true);
					}
					
					handler.obtainMessage(3, 1, -1,
							"The radio is being configured for ad-hoc mode and will look for a network.\nThis should take " + device.getDeviceSleep()/1000 + " seconds")
							.sendToTarget();
					
					NetworkThread.sleep(device.getDeviceSleep()); // (80000);

					if (!device.doesNetworkExist()) {
						device.disconnectDevice();
						
						handler.obtainMessage(3, -1, -1,
								"There was an error initializing the wifi\n. Go to 'disconnect' in the menu then try to 'connect' again.\nIf errors persist, try rebooting the device.")
								.sendToTarget();						

					} else {
						
						ni.setNetworkUp(true);
						
						startDaemon();
						
						handler.obtainMessage(3, 2, -1,
						"The device is now advertising the network. It will take 7 seconds to pair with other devices")
						.sendToTarget();		
					
						NetworkThread.sleep(7000);  
						
						handler.obtainMessage(3, 3, -1,
						"The device is sending discovery messages to see if there are any neighbors")
						.sendToTarget();
						
						WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

						// Get WiFi status
						WifiInfo info = wifi.getConnectionInfo();
						
						System.out.println("MAC: " + info.getMacAddress());
						
						
						DataObject dataObject = new DataObject();
						dataObject.setDestinationAddress(Constants.BROADCAST);
						dataObject.setOrginAddress(ni.getMyIP());
						dataObject.setLocalRank(ni.getLocalRank());
						dataObject.setMessageType(Constants.JOIN);
						sendMessage(dataObject);	
						
						NetworkThread.sleep(5000);
						
						//else 
						if (!ni.isAcknowledged()) { 	
							//device specific if first to join
							
							ni.setMyIP(11);
							device.changeIP(11);
							ni.network.add(new NetworkNode(ni.getLocalRank(),Constants.BROADCAST));
							
							// for eris type, which can continue to 
							if (device.deviceCanAdvertiseNetwork())
							{								
								handler.obtainMessage(3, -1, -1,
								"There are no other devices available, you are advertising the network")
								.sendToTarget();
								
								handler.obtainMessage(2, -1, -1, Constants.BASE_ADDRESS + "11").sendToTarget(); 	
								
							} else {
								
								handler.obtainMessage(3, -1, -1,
								"There are no other devices available, you are advertising the network for 1 minute")
								.sendToTarget();
								
								handler.obtainMessage(2, -1, -1, Constants.BASE_ADDRESS + Constants.HUB_IP).sendToTarget(); 	
								
								NetworkThread.sleep(50000);
								
								netOff();

							}	

						}		
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.obtainMessage(3, -1, -1,
						"There was an error initializing the wifi\n. Go to 'disconnect' in the menu then try to 'connect' again.\nIf errors persist, try rebooting the device.")
						.sendToTarget();
			}
		}// end run
	}// end inner class

	public void test() {
		System.out.println("service connectewd!!");
	}

}
