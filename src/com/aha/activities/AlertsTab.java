package com.aha.activities;

import com.aha.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class AlertsTab extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_container);
	    
	    Bundle b = getIntent().getExtras();

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, NetworkActivity.class);
	    //intent.putExtras(b);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("network").setIndicator("Network",
	                      res.getDrawable(R.drawable.ic_tray_tab))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, AlertsActivity.class);
	    spec = tabHost.newTabSpec("alerts").setIndicator("Alerts",
	                      res.getDrawable(R.drawable.ic_tray_tab))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}	

}
