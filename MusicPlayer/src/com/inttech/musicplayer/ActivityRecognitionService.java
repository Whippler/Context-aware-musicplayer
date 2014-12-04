package com.inttech.musicplayer;

import org.json.JSONObject;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ActivityRecognitionService extends IntentService{

	public static final String prefKeyType = "PREVIOUS_ACTIVITY";
	private SharedPreferences mPrefs;
	
	public ActivityRecognitionService() {
		super("Whatever");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		mPrefs = getApplicationContext().getSharedPreferences(RecognitionUtilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		
		if(ActivityRecognitionResult.hasResult(intent)){
			// Get the update
			ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
			Log.i("Service", getType(result.getMostProbableActivity().getType()) +"\t" + result.getMostProbableActivity().getConfidence());
			
			// Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            
            int activityType = mostProbableActivity.getType();
            
            //If there's no activity yet or the new one is greater than the old one
            if (!mPrefs.contains(prefKeyType)){
            	storeActivity(activityType);
            	RecognitionUtilities.sendMessage(this, toJson(mostProbableActivity).toString());
            }
            else if (activityChanged(activityType) && mostProbableActivity.getConfidence() > 50){
            	storeActivity(activityType);
            	RecognitionUtilities.sendMessage(this, toJson(mostProbableActivity).toString());
            }
            
		}
		
	}
	
	/**Detects if the new values is different form the last activity */
	private boolean activityChanged(int currentType) {
		
		if(mPrefs.getInt(prefKeyType, DetectedActivity.UNKNOWN) != currentType){
			return true;
		}
		else{
			return false;
		}
	}
	
	/** Store the last activity value */
	public void storeActivity(int activityType){

		Editor editor = mPrefs.edit();
   	 	editor.putInt(prefKeyType, activityType);
   	 
   	 	editor.commit();
   	 	
	}
	
	private String getType(int type){
		if(type == DetectedActivity.UNKNOWN)
			return "Unknown";
		else if(type == DetectedActivity.IN_VEHICLE)
			return "Vehicle";
		else if(type == DetectedActivity.ON_BICYCLE)
			return "Bicycle";
		else if(type == DetectedActivity.ON_FOOT)
			return "Foot";
		else if(type == DetectedActivity.STILL)
			return "Still";
		else if(type == DetectedActivity.TILTING)
			return "Tilting";
		else
			return "";
	}
	
	private JSONObject toJson(DetectedActivity mostProbableActivity){
		JSONObject retVal = new JSONObject();
		try {
			retVal.put("MessageType", "ActivityRecognition");
			retVal.put("ActivityID", mostProbableActivity.getType());
			retVal.put("ActivityName", getType(mostProbableActivity.getType()));
			retVal.put("ActivityConfidence", mostProbableActivity.getConfidence());
			return retVal;
		    
		}
		catch(Exception e){
			Log.e("ActivityRecognitionService", "Error trying to parse to Json");
			  
			return null;
		}
		
	}

}
