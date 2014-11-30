package com.example.geolocation;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationDriver implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private static final int UPDATE_INTERVAL_IN_MILISECONDS = 5000;
	private static final int FAST_INTERVAL_CEILING_IN_MILLISECONDS = 1000;
	
	Context context;
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    public LocationDriver(Context context){
    	this.context = context;
    	
    	mLocationRequest = LocationRequest.create(); 
    	mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILISECONDS); //update interval
    	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Accuracy level
    	mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    	mLocationClient = new LocationClient(context, this, this);
    }
    
    public void connect(){
    	
    	mLocationClient.connect();
    	
    }
    
    public void disconnect(){
    	mLocationClient.disconnect();
    }
    
    public boolean isConnected(){
    	return mLocationClient.isConnected();
    }
    
    private Location getLocation(){
    	Location currentLocation = null;
    	
    	if(GoogleServicesValidator()){
    		currentLocation = mLocationClient.getLastLocation();
    	}
    	
    	return currentLocation;
    }
    
    public String getLocality(){
    	
    	String retVal;
    	
    	Geocoder geocoder= new Geocoder(context, Locale.ENGLISH);
    	try{
    		Location location = getLocation();
    		Log.i("getAdress", location.getLatitude()+"");
    		List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
       	 
      	  if(addresses != null) {
      	  
      		  Address fetchedAddress = addresses.get(0);
      		  StringBuilder strAddress = new StringBuilder();
      	   
      		  retVal = fetchedAddress.getSubAdminArea() + ", " + fetchedAddress.getCountryName();
      		  
      		  /*for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
      			  	strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
      		  }*/
      		  
      	  }
      	  else{
      		  retVal="Location was not found";
      	  }
      	  
      	  return retVal;
      	  
    	}
    	catch(Exception e){
    		
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
                // No geocoder is present. Issue an error message
                return "No geocoder avaiable on your device";
            }
    		else
    			return e.getLocalizedMessage();
    	}
    	
    }
    
    public void startPeriodicUpdates() {
    	if(GoogleServicesValidator()){
    		mLocationClient.requestLocationUpdates(mLocationRequest, this);
    	}
    }
    
    public void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }
    
    private void sendMessage(String Message) {
    	  Intent intent = new Intent("MainActivity");
    	  // add data
    	  intent.putExtra("TEST", Message);
    	  LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
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
		sendMessage(getLocality());
	}

}
