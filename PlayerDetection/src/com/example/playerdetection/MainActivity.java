package com.example.playerdetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView textUI; // Points to the textview in the UI
	//private BroadcastReceiver receiver;
	private IntentFilter intFilter;
	
	private ActivityRecognitionDriver arDriver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textUI = (TextView) findViewById(R.id.textUI);
		
		arDriver = new ActivityRecognitionDriver(this);
		intFilter = new IntentFilter();
		intFilter.addAction(arDriver.MESSAGES);
		
		if(arDriver.GoogleServicesValidator()){
			textUI.setText("Well, it recognizes the play services");
			
			if(!arDriver.isConnected()){
				arDriver.connect();
				textUI.setText("it is connected now");
			}
			
		}
		else{
			textUI.setText("Nop, there's something wrong in here");
		}
		
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String received = intent.getStringExtra("TEST");
			textUI.setText(received);
		}
		
	};	

	@Override
    public void onResume(){
		super.onResume();
		registerReceiver(receiver, intFilter);
		Log.i("onResume", "receiver has been register");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		arDriver.disconnect();
		unregisterReceiver(receiver);
		arDriver = null;
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
