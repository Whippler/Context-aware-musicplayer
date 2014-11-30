package com.example.geolocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView textUI;
    LocationDriver locDriver;
    
    IntentFilter filter;
    public final static String MESSAGES = "com.example.geolocation.MESSAGES";
	
    private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String received = intent.getStringExtra("TEST");
			textUI.setText(received);
		}
		
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textUI = (TextView) findViewById(R.id.textUI);
		
		locDriver = new LocationDriver(this);
		locDriver.connect();
		
		filter = new IntentFilter(MESSAGES);
		filter.addAction(MESSAGES);
		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(locDriver.isConnected()){ // this should be part of a bottom to allow location or not
			locDriver.startPeriodicUpdates();
		}
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
			      new IntentFilter("MainActivity"));
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(locDriver.isConnected()){ // when the app is paused, the gps will stop working
			locDriver.stopPeriodicUpdates();
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		if(locDriver.isConnected()){
			locDriver.stopPeriodicUpdates();
		}
		locDriver.disconnect();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
