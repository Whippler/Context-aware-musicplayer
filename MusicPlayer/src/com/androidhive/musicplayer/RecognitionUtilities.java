package com.androidhive.musicplayer;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class RecognitionUtilities {
	//Important values that could be modified
	private static final int DETECTION_INTERVAL_SECONDS = 10;
	private static final int LOCATION_UPDATE_INTERVAL_IN_SECONDS = 120;
	private static final int LOCATION_FAST_INTERVAL_CEILING_IN_SECONDS = 1;
	public static final int NOISE_THRESHOLD = 10;
	
	//Shared constants
	public final static String MESSAGES = "com.androidhive.musicplayer.MESSAGES";
	public final static String SHARED_PREFERENCES = "com.androidhive.musicplayer.SHARED_PREFERENCES";
	
	//Constant
	public static final int MILLISECONDS_PER_SECOND = 1000;
	
	//Activity recognition
	public static final int ACTIVITY_DETECTION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
	
	//Location
	public static final int LOCATION_UPDATE_INTERVAL_IN_MILISECONDS = MILLISECONDS_PER_SECOND * LOCATION_UPDATE_INTERVAL_IN_SECONDS;
	public static final int LOCATION_FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * LOCATION_FAST_INTERVAL_CEILING_IN_SECONDS;
	
	public static boolean GoogleServicesValidator(Context context){
		
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS){
			return true;
		}
		else{
			return false;
		}
	}

	public synchronized static void sendMessage(Context context, String Message) {
  	  Intent intent = new Intent("MainActivity");
  	  // add data
  	  intent.putExtra("Message", Message);
  	  LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  }

	
}
