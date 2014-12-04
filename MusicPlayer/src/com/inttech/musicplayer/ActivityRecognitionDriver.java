//package com.example.playerdetection;
package com.inttech.musicplayer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ActivityRecognitionDriver implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{

	private Context context;
	private PendingIntent pIntent;
	private ActivityRecognitionClient arClient;

	public ActivityRecognitionDriver(Context context){
		this.context = context;
		this.arClient = null;
		this.pIntent = null;
	}
	
	/** Connects the client to Google Play services and start the Activity recognition*/
	public void connect(){
		if(arClient == null){
			arClient = new ActivityRecognitionClient(context, this, this);
			arClient.connect();
		}
		else{
			arClient.connect();
		}
	}
	
	public boolean isConnected(){
		return arClient.isConnected();
	}
	
	/** Disconnects the client to Google Play services */
	public void disconnect(){
		if(arClient!=null){
			arClient.removeActivityUpdates(pIntent);
			arClient.disconnect();
		}
		arClient=null;
	}
	
	private void requestUpdates(){
		
		Intent intent = new Intent(context, ActivityRecognitionService.class);
		pIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		arClient.requestActivityUpdates(RecognitionUtilities.ACTIVITY_DETECTION_INTERVAL_MILLISECONDS, pIntent);
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		requestUpdates();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}
