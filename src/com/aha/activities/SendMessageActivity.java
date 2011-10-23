package com.aha.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aha.R;
import com.aha.services.NetService;
import com.aha.services.NetService.LocalBinder;

public class SendMessageActivity extends Activity implements OnClickListener {
	

	Button sendB;
	EditText address, message;
    NetService mService;
    AlertDialog alert;
    boolean mBound = false;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage);
        sendB = (Button)this.findViewById(R.id.SendB);
        address = (EditText)this.findViewById(R.id.Address);
        message = (EditText)this.findViewById(R.id.Message);
        //globalB = (Button)this.findViewById(R.id.GlobalB);
                   
        sendB.setOnClickListener(this);
        
    }
        
    public void onClick(View v) {
        	
        switch (v.getId()) {	    	        
            case R.id.SendB:
    	        if (mBound) {

    	        	System.out.println("bound to service");
    	        	
    	        	mService.test();
    	        	
    	        	try {
    	        		
    	        		String msg = message.getText().toString();
    		        	mService.sendMessage(msg);
    		        	message.setText("");
    	        		
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    		}	
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
	
	
	

}
