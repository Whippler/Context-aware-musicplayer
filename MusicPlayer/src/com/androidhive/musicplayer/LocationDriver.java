package com.androidhive.musicplayer;


import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationDriver implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private Context context;
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    public LocationDriver(Context context){
    	this.context = context;
    	
    	mLocationRequest = LocationRequest.create(); 
    	mLocationRequest.setInterval(RecognitionUtilities.LOCATION_UPDATE_INTERVAL_IN_MILISECONDS); //update interval
    	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Accuracy level
    	mLocationRequest.setFastestInterval(RecognitionUtilities.LOCATION_FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    	mLocationClient = new LocationClient(context, this, this); //Create a location request with default parameters.
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		startPeriodicUpdates();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		stopPeriodicUpdates();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		JSONObject temp = getLocality();
		if(temp!=null){
			RecognitionUtilities.sendMessage(context, temp.toString());
		}
	}
	
	/** Connects the client to Google Play services */
	public void connect(){
    	mLocationClient.connect();
    }
	
	public boolean isConnected(){
    	return mLocationClient.isConnected();
    }
	
	/** Disconnects the client to Google Play services */
	public void disconnect(){
    	mLocationClient.disconnect();
    }
	
	/** Get the actual location */
	private Location getLocation(){
	    	Location currentLocation = null;
	    	
	    	if(RecognitionUtilities.GoogleServicesValidator(context)){
	    		currentLocation = mLocationClient.getLastLocation();
	    	}
	    	else{
	    		Toast.makeText(context, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
		}
		
		return currentLocation;
	}
	
	/** Get the address from the location */
	public JSONObject getLocality(){
    	
		JSONObject retVal = null;
		Geocoder geocoder= new Geocoder(context, Locale.ENGLISH);
		
		try{
			Location location = getLocation();
			Log.i("getAdress", location.getLatitude()+"");
				
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   	 
			if(addresses != null) {
					  
					// get the address
					Address fetchedAddress = addresses.get(0); 
					retVal = new JSONObject();
					
					try {
						retVal.put("MessageType", "Location");
						retVal.put("Country", fetchedAddress.getCountryName());
						retVal.put("Code", fetchedAddress.getCountryCode());
						retVal.put("City", fetchedAddress.getSubAdminArea());
						retVal.put("Locality", fetchedAddress.getLocality());
						retVal.put("Coordinates", fetchedAddress.getLatitude()+","+fetchedAddress.getLongitude());
					}
					catch(Exception e){
						
						return null;
					}				
					
			}
		  
			return retVal;
		  
		}
    	catch(Exception e){
    		
    		return null;
    		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
                return null;
            }
    		else
    			return e.getLocalizedMessage();*/
    	}
    }

	/** Requests location updates */
	public void startPeriodicUpdates() {
    	if(RecognitionUtilities.GoogleServicesValidator(context)){
    		mLocationClient.requestLocationUpdates(mLocationRequest, this); //From here it is possible to create an intent
    	}
    }
    
    public void stopPeriodicUpdates() {
    	if(mLocationClient!=null){
    		mLocationClient.removeLocationUpdates(this);
    	}
    }

}
