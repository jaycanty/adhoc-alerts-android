package com.aha.activities;

import java.util.Vector;

import android.app.Activity;





import com.aha.R;
import android.widget.ArrayAdapter;

import com.aha.models.AppInfo;
import com.aha.models.Constants;
import com.aha.models.DataObject;
import com.aha.models.NetworkInfo;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class InfoActivity extends Activity {
	
	
	TextView statusTV;
	TextView ipTV;
    NetService mService;
    AlertDialog alert;
    boolean mBound = false;
    ListView lv;
    Handler handler;
    public static boolean inFocus;

    
    View stage1;
    View stage2;
    View stage3;
    View stage4;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        
        stage1 = (View)this.findViewById(R.id.StageView1);
        stage2 = (View)this.findViewById(R.id.StageView2);
        stage3 = (View)this.findViewById(R.id.StageView3);
        stage4 = (View)this.findViewById(R.id.StageView4);
        
        statusTV = (TextView)this.findViewById(R.id.StatusTV);
        ipTV = (TextView)this.findViewById(R.id.IPTV);
       
       AppInfo.getInstance();
       ipTV.setText(Constants.BASE_ADDRESS + NetworkInfo.getInstance().getInitIP());
       inFocus = false;
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
	        	            String ip = (String) msg.obj;
	        	            if (ip.length() > 0)
	        	            	ipTV.setText(ip);
	        	        break;
                    	case 3:
	        	            statusTV.setText((String) msg.obj);	 
	        	            colorProgressBar(msg.arg1);
        	            break;
                    }
                }
            };            
            
            AppInfo ai = AppInfo.getInstance();
            
	        ai.setNetworkContext(InfoActivity.this);
	        ai.setNetworkHandler(handler);             
        }

        //@Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
        
        private void colorProgressBar(int stage)
        {
            switch (stage) {
	            case 1:
	            	stage1.setBackgroundColor(0x88913333);
	            break;
	            case 2:
	            	stage2.setBackgroundColor(0x8831547b);
	            break;
	            case 3:
	            	stage3.setBackgroundColor(0x88efed62);
	            break;
	            case 4:
	            	stage4.setBackgroundColor(0x88477b31);
	            break;
	            case 5:
	            	stage1.setBackgroundColor(0xff545a64);
	               	stage2.setBackgroundColor(0xff545a64);
	                stage3.setBackgroundColor(0xff545a64);
	               	stage4.setBackgroundColor(0xff545a64); 
	            break;
            }
        }        
        
        
    };	
    
    
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
        case R.id.connect:
	        if (mBound) {	    	        	
	        	try {
	        		
	    			final String[] items = { "30minutes", "1hour", "2hours", "3hours" };
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	builder.setTitle("How long are you staying?");
		        	builder.setItems(items, new DialogInterface.OnClickListener() {
		        	    public void onClick(DialogInterface dialog, int item) { 
		        	    	mService.startNetwork(item);
		        	    }
		        	});
		        	alert = builder.create();        
		        	alert.show();
		        	
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	        }
            return true;            
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
}
