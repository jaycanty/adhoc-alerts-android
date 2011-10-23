package com.aha.activities;

import com.aha.R;
import android.widget.ArrayAdapter;
import com.aha.services.NetService;
import com.aha.services.NetService.LocalBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AlertsActivity extends Activity implements OnClickListener {
	
	
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
    private ArrayAdapter<String> conversationArray;
    ListView lv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alerts);
        tv = (TextView)this.findViewById(R.id.TextStatus); 		       
        localB = (Button)this.findViewById(R.id.LocalB);
        globalB = (Button)this.findViewById(R.id.GlobalB);
        
        System.out.println("Activity Created!!");
        
        localB.setOnClickListener(this);
        globalB.setOnClickListener(this);
        
        
       conversationArray = new ArrayAdapter<String>(this, R.layout.message);
       lv = (ListView)this.findViewById(R.id.ListView);
       lv.setAdapter(conversationArray);        
        
    }
        
    public void onClick(View v) {
        	
        switch (v.getId()) {	    	        
            case R.id.LocalB:
    	        if (mBound) {	    	        	
    	        	try {
	        	    	Intent intent = new Intent(AlertsActivity.this, SendMessageActivity.class);
    	                startActivity(intent); 
    		        	
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    		}	
    	        } 
    	        break;
            case R.id.GlobalB:
    	        if (mBound) {
    	            mService.startDaemon(this, handler);
    	            //tv.setText(msg);
    	        }
    	        break;	    	        
        }	            		        	        
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        //System.out.println("Trying to bind to the service");
        
        try
        {
	        Intent intent = new Intent(this, NetService.class);
	        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
        	getApplicationContext().unbindService(mConnection);
            mBound = false;
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }  
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.disconnect:
            mService.netOff();
            return true;
        default:
            return super.onOptionsItemSelected(item);
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
    
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
	            case 3:
	                String readMessage = (String) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                conversationArray.add("You:  " + readMessage);
	            break;
            }
        }
    }; 	

}
