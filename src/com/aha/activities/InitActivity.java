package com.aha.activities;

import com.aha.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.aha.services.NetService;
import com.aha.services.NetService.LocalBinder;

public class InitActivity extends Activity implements OnClickListener{
		
		Button infoB;
		Button wifiOnB;
		Button wifiOffB;
		Button localB;
		Button staticB;
		Button globalB;
		TextView tv;
	    NetService mService;
	    AlertDialog alert;
	    boolean mBound = false;		
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        //tv = (TextView)this.findViewById(R.id.TextStatus); 		       
	        localB = (Button)this.findViewById(R.id.LocalB);
	        globalB = (Button)this.findViewById(R.id.GlobalB);
	        
	        localB.setOnClickListener(this);
	        globalB.setOnClickListener(this);
	    }
	        
	    public void onClick(View v) {
            	
            switch (v.getId()) {	    	        
                case R.id.LocalB:
	    	        if (mBound) {	    	        	
	    	        	try {
	    	        		
	    	    			final String[] items = { "30minutes", "1hour", "2hours", "3hours" };
	    		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		        	builder.setTitle("How long are you staying?");
	    		        	builder.setItems(items, new DialogInterface.OnClickListener() {
	    		        	    public void onClick(DialogInterface dialog, int item) { 
	    		        	    	String msg = mService.advertiseNet();
	    		        	    	Intent intent = new Intent(InitActivity.this, AlertsTab.class);
	    	    	                startActivity(intent); 
	    		        	    }
	    		        	});
	    		        	alert = builder.create();        
	    		        	alert.show();
	    		        	

	    		        	
	    	    		} catch (Exception e) {
	    	    			e.printStackTrace();
	    	    		}
	    	        		
	    	        		
	    	        }
	    	        break;
                case R.id.GlobalB:
	    	        if (mBound) {
	    	            String msg = mService.netOff();
	    	            //tv.setText(msg);
	    	        }
	    	        break;	    	        
            }	            		        	        
	    }

	    @Override
	    protected void onStart() {
	        super.onStart();
	        // Bind to LocalService
	        Intent intent = new Intent(this, NetService.class);
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        // The activity has become visible (it is now "resumed").
	        Intent intent = new Intent(this, NetService.class);
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);	        
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        if (mBound) {
	            unbindService(mConnection);
	            mBound = false;
	        }	        
	        // Another activity is taking focus (this activity is about to be "paused").
	    }	    
	    
	    @Override
	    protected void onStop() {
	        super.onStop();
	        // Unbind from the service
	        if (mBound) {
	            unbindService(mConnection);
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
	        }

	        //@Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            mBound = false;
	        }
	    };	    
	    
	    
	    
/*		
	    // Called when the activity is first created. 
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        //wfMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        System.out.println("This is a check 1,2");       
	        
	        et = (EditText)this.findViewById(R.id.EditText); 
	       
	        sendB = (Button)this.findViewById(R.id.SendB);
	        sendB.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            		            	
	            	startService(new Intent(NetActivity.this, NetService.class));

	            }
	        });  
	          
	    }
	    
*/	    
	    
	    
	    
	    

	}