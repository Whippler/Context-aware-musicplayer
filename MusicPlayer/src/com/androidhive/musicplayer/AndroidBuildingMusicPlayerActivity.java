package com.androidhive.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AndroidBuildingMusicPlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    	private String currentActivity = "Unknown";
    	private String currentLocation;
        private TextView activityField;
        /** Message Handler*/
        private IntentFilter filter;
        private BroadcastReceiver receiver;
        private SharedPreferences mPrefs;
        private Editor prefEditor;
        private double currentLat;
        private double currentLon;
        private double lat1, lon1, lat2, lon2;
    
	//Location
	LocationDriver locDriver;	
	//Activity
	private ActivityRecognitionDriver arDriver;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		mPrefs = getApplicationContext().getSharedPreferences("test", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = mPrefs.edit();
                float homeLat = new Float(getResources().getString(R.string.default_lat));
                float homeLon = new Float(getResources().getString(R.string.default_lon));
                float workLat = new Float(getResources().getString(R.string.default_lat2));
                float workLon = new Float(getResources().getString(R.string.default_lon2));
                lat1 = new Double(mPrefs.getFloat("homeLat", homeLat));
                lon1 = new Double(mPrefs.getFloat("homeLon", homeLon));
                lat2 = new Double(mPrefs.getFloat("workLat", workLat));
                lon2 = new Double(mPrefs.getFloat("workLon", workLon));
                currentLat = lat1;
                currentLon = lon1;
		
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		activityField = (TextView) findViewById(R.id.activityField);
		
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager(getApplicationContext());
		utils = new Utilities();
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		
		// Getting all songs list
		songsList = songManager.getPlayList();
                songManager.updateFilteredList("default", currentLat, currentLon);
		
		// By default DON'T play first song
		//playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
                                playSong(nextActivitySongIndex(currentSongIndex));
				
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isRepeat){
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isShuffle){
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					isShuffle= true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		});
		
		locDriver = new LocationDriver(this);
		arDriver = new ActivityRecognitionDriver(this);
		
		//Message handler
		filter = new IntentFilter(RecognitionUtilities.MESSAGES);
		filter.addAction(RecognitionUtilities.MESSAGES);
		
		if(RecognitionUtilities.GoogleServicesValidator(this)){
			activityField.setText("Recognizing the play services");
			locDriver.connect();
			arDriver.connect();
			activityField.setText("Waiting for activities ... ");
			
		}
		else{
			activityField.setText("Nop, there's something wrong in here");
		}

	        receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                            Log.i("onReceive", "Received a broadcast");
                            // TODO Auto-generated method stub
                            String received = intent.getStringExtra("Message");
                            try {
                                JSONObject json = new JSONObject(received);
                                //String type = json.getString("MessageType");
                                if(json.get("MessageType").equals("ActivityRecognition")){
                                    String activity = json.getString("ActivityName");
                                    if(!currentActivity.equals(activity)) {
                                        songManager.updateFilteredList(activity, currentLat, currentLon);
                                    }
                                    currentActivity = activity;
                                    received = (currentLocation!=null) ? currentActivity + ", in " + currentLocation : currentActivity;
                                }
                                else if(json.get("MessageType").equals("Location")){
                                        String[] coordinateString;
                                        Double lat, lon;
                                	currentLocation = json.getString("City");
                                	currentLocation += ", " + json.getString("Country");
                                	received = (currentActivity!=null) ? currentActivity + ", in " + currentLocation : currentLocation;
                                        coordinateString = json.getString("Coordinates").split(",");
                                        lat = new Double(coordinateString[0]);
                                        lon = new Double(coordinateString[1]);
                                        if(lat != currentLat || lon != currentLon) {
                                            songManager.updateFilteredList(currentActivity, currentLat, currentLon);
                                        }
                                        currentLat = lat;
                                        currentLon = lon;
                                }
                                
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            received = received + " (" + currentLat + ", "+ currentLon +")";
                            String nearestLoc = songManager.checkLocation();
                            if(!nearestLoc.equals("other")) {
                                received = received + " near " + nearestLoc;
                            }
                            activityField.setText(received);
                    }
                };	
	
		registerReceiver(receiver, filter);
		//Log.i("onCreate", "receiver has been register");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// Register the receiver
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("MainActivity"));
		Log.i("onCreate", "receiver has been register");
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

	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
        protected void onActivityResult(int requestCode,
                                         int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == 100){
                     currentSongIndex = data.getExtras().getInt("songIndex");
                     // play selected song
                 playSong(currentSongIndex);
            }
     
        }

        private int nextActivitySongIndex(int songIndex) {
            int new_index;
            ArrayList<Integer> filteredList = songManager.getFilteredList();
            new_index = filteredList.indexOf(songIndex);
            // check for repeat is ON or OFF
            if(isRepeat){
                    // repeat is on play same song again
                    currentSongIndex = songIndex;
            } else if (new_index == -1){
                    currentSongIndex = filteredList.get(0);
            } else if(isShuffle){
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    new_index = rand.nextInt((filteredList.size() - 1) - 0 + 1) + 0;
                    currentSongIndex = filteredList.get(new_index);
            } else{
                    // no repeat or shuffle ON - play next song
                    if(new_index < (filteredList.size() - 1)){
                            new_index = new_index + 1;
                    }else{
                            // play first song
                            new_index = 0;
                }
                currentSongIndex = filteredList.get(new_index);
            }
            return currentSongIndex;
        }

	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void playSong(int songIndex){
		// Play song
		try {
        	mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
        	songTitleLabel.setText(songTitle);
			
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
                           try {
                               long totalDuration = mp.getDuration();
                               long currentDuration = mp.getCurrentPosition();
                              
                               // Displaying Total Duration time
                               songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
                               // Displaying time completed playing
                               songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
                               
                               // Updating progress bar
                               int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                               //Log.d("Progress", ""+progress);
                               songProgressBar.setProgress(progress);
                           }
                           catch(IllegalStateException e){
                           }
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
            playSong(nextActivitySongIndex(currentSongIndex));
	}
	
	@Override
	 public void onDestroy(){
            super.onDestroy();
	    mp.release();
            arDriver.disconnect();
            unregisterReceiver(receiver);
            arDriver = null;
	 }
	
}
