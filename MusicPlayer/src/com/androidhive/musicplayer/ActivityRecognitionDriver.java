//package com.example.playerdetection;
package com.androidhive.musicplayer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityRecognitionDriver implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String MESSAGES = "com.example.playerdetection.ACTIVITY_RECOGNITION_DATA";
	public static final int TIME = 1; // time in seconds that the program will request updates
	private Context context;
	private PendingIntent pIntent;
	private ActivityRecognitionClient arClient;
	
	
	public ActivityRecognitionDriver(Context context){
		this.context = context;
		this.arClient = null;
		this.pIntent = null;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.e("Driver", "Connection Failed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		requestUpdates();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		//DO NOTHING HERE
	}
	
	public boolean isConnected(){
		boolean retVal = false;
		
		if(arClient!=null){
			retVal = arClient.isConnected();
		}
		return retVal;
	}
	
	public void connect(){
		if(arClient == null){
			arClient = new ActivityRecognitionClient(context, this, this);
			arClient.connect();
		}
		else{
			arClient.connect();
		}
		
	}
	
	public void disconnect(){
		if(arClient!=null){
			arClient.removeActivityUpdates(pIntent);
			arClient.disconnect();
		}
		arClient=null;
	}
	
	/** Request updates based in the assigned time (in seconds)
      * The PendingIntent sends updates to ActivityRecognitionIntentService
      */
	private void requestUpdates(){
		
		Intent intent = new Intent(context, ActivityRecognitionService.class);
		pIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		arClient.requestActivityUpdates( (1000*TIME), pIntent );
		
		// Disconnect from the client
		//disconnect();
	}
	
	public boolean GoogleServicesValidator(){
		
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS){
			return true;
		}
		else{
			Toast.makeText(context, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
