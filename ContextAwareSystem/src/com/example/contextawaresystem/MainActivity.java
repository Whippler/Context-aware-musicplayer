package com.example.contextawaresystem;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	// TextViews
	private TextView locationText;
	private TextView activityText;
	
	//Location
	LocationDriver locDriver;
	
	//Activity
	private ActivityRecognitionDriver arDriver;
	
	/** Message Handler*/
	IntentFilter filter;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String received = intent.getStringExtra("Message");	
			try{
				JSONObject json = new JSONObject(received);
				if(json.get("MessageType").equals("Location")){
					locationText.setText(json.toString());
				}
				else if(json.get("MessageType").equals("ActivityRecognition")){
					
					activityText.setText(json.toString());
					
				}
				
			}
			catch(Exception e){
				locationText.setText("Problem parsin the message");
				Log.e("Receiver", e.getMessage());
			}
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationText = (TextView) findViewById(R.id.Location);
		activityText = (TextView) findViewById(R.id.Activity);
		
		locDriver = new LocationDriver(this);
		arDriver = new ActivityRecognitionDriver(this);
		locDriver.connect();
		arDriver.connect();
		
		//Message handler
		filter = new IntentFilter(RecognitionUtilities.MESSAGES);
		filter.addAction(RecognitionUtilities.MESSAGES);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// Register the receiver
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("MainActivity"));
	}

	@Override
	public void onPause(){
		super.onPause();
		// Stop receiving updates from the location
		if(locDriver.isConnected()){ 
			locDriver.stopPeriodicUpdates();
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		// Stop updates from the location
		if(locDriver.isConnected()){
			locDriver.stopPeriodicUpdates();
		}
		
		// Disconnect from the google play services
		locDriver.disconnect();
		arDriver.disconnect();
		
		// Unregister the receiver
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
