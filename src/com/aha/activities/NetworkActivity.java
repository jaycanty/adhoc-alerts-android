package com.aha.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.aha.R;
import android.widget.ArrayAdapter;

import com.aha.models.AppInfo;
import com.aha.models.Constants;
import com.aha.models.DataObject;
import com.aha.models.NetworkInfo;
import com.aha.models.NetworkNode;
import com.aha.services.NetService;
import com.aha.services.NetService.LocalBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NetworkActivity extends Activity implements OnItemClickListener {

    NetService mService;
    AlertDialog alert;
    boolean mBound = false;
    ListView lv;
    Handler handler; 
    CustomAdapter networkAdapter;
    public static boolean inFocus;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network);
         
       lv = (ListView)this.findViewById(R.id.ListView);
       networkAdapter = new CustomAdapter();
       lv.setAdapter(networkAdapter); 
       lv.setOnItemClickListener(this);          
       inFocus = false;
       
       NetworkInfo ni = NetworkInfo.getInstance();
       
       ni.network.add(new NetworkNode(0,255));
       
       NetworkNode nn = new NetworkNode(0,13);
       nn.setHasNew(true);
       ni.network.add(nn);
       
       DataObject doo = new DataObject();
       
       doo.setMessage("Water shippment at the post");
       
       Vector<DataObject> v = new Vector<DataObject>();
       
       v.add(doo);
    		   
       ni.conversations.put(13, v);
       
    }
        
    public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
    		
    	  String s = (String)((TextView) view).getText();
    	  String[] sa = s.split(" : ");
    	  int orginIP = 0;
    	  if (sa[0].equalsIgnoreCase("BROADCASTS"))
    		  orginIP = Constants.BROADCAST;
    	  else
    		  orginIP = Integer.parseInt(sa[0]);
    	  
    	  NetworkInfo ni = NetworkInfo.getInstance();    	  
    	  ni.getNetworkNode(orginIP).setHasNew(false);
    	     	  
          Intent intent = new Intent(NetworkActivity.this, ConversationActivity.class);
          intent.putExtra("originIP", orginIP);
          startActivity(intent);            
    }  

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService        
        try
        {
	        Intent intent = new Intent(this, NetService.class);
	        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	        networkAdapter.notifyDataSetChanged();
	        //inFocus = true;
	        
        } catch (Exception e) {
        	
        	e.printStackTrace();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Intent intent = new Intent(this, NetService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);	        
        networkAdapter.notifyDataSetChanged();
        inFocus = true;
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
        	getApplicationContext().unbindService(mConnection);
            mBound = false;
            inFocus = false;
        }	        
        // Another activity is taking focus (this activity is about to be "paused").
    }	    
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
        	getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        //@Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
	                    case 2:
	                    	networkAdapter.notifyDataSetChanged();                    
	                    break;
        	            case 3:
        	            	networkAdapter.notifyDataSetChanged();
        	            break;
                    }
                }
            };            
            
            AppInfo ai = AppInfo.getInstance();
            
	        ai.setAlertsContext(NetworkActivity.this);
	        ai.setAlertsHandler(handler);
        }

        //@Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };		
    
	private class CustomAdapter extends ArrayAdapter<NetworkNode> {
	    CustomAdapter() {
	      super(NetworkActivity.this, R.layout.message, R.id.TextView, NetworkInfo.getInstance().network);
	    }
	
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {     
	      View row = convertView;
	
	      if (row == null) {
	        // This gives us a View object back which, in reality, is our LinearLayout with 
	        // an ImageView and a TextView, just as R.layout.row specifies.
	        LayoutInflater inflater = getLayoutInflater();      
	        row = inflater.inflate(R.layout.message, parent, false);
	      }
	      	      
			TextView label = (TextView) row.findViewById(R.id.TextView);
			
	        NetworkInfo ni = NetworkInfo.getInstance();
	        NetworkNode netNode = ni.network.get(position);
			int ip = netNode.getIp();

			if (netNode.hasNew())
			{
				label.setBackgroundColor(0xFF656565);
				label.setTextColor(Color.RED);
			} else {
				label.setBackgroundColor(Color.BLACK);
				label.setTextColor(Color.WHITE);
			}
			if (ip == Constants.BROADCAST)
			{
				if(ni.conversations.containsKey(ip))
				{
					//networkArray.add( );
					int count = ni.conversations.get(ip).size();
					label.setText("BROADCASTS : " + count);        		
					
				} else
					label.setText("BROADCASTS : -");				
								
			} else {
		
				if(ni.conversations.containsKey(ip))
				{
					//networkArray.add( );
					int count = ni.conversations.get(ip).size();
					label.setText("" + ip + " : " + count);        		
					
				} else
					label.setText("" + ip + " : -");
			}
	      return row;       
	    }
	  }      
}
