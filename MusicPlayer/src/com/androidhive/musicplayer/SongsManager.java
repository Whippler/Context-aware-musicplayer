package com.androidhive.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.os.Environment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("/storage/sdcard0");
	final static String MEDIA_PATH2 = new String("/storage/SD card/Media/Music/Arch Enemy");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private ArrayList<Integer> filteredList = new ArrayList<Integer>();
        public double currentLat;
        public double currentLon;
        public double lat1, lon1, lat2, lon2;
        private SharedPreferences mPrefs;
        private Context context;
        final static double LIMIT = 0.3; //km
        public String currentCustomLocation;
	
	// Constructor
	public SongsManager(Context context){
		mPrefs = context.getSharedPreferences("test", Context.MODE_PRIVATE);
                float defaultLat = new Float(context.getResources().getString(R.string.default_lat));
                float defaultLon = new Float(context.getResources().getString(R.string.default_lon));
                lat1 = new Double(mPrefs.getFloat("homeLat", defaultLat));
                lon1 = new Double(mPrefs.getFloat("homeLon", defaultLon));
                lat2 = new Double(mPrefs.getFloat("workLat", defaultLat));
                lon2 = new Double(mPrefs.getFloat("workLon", defaultLon));
                this.context = context;
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
            File home = new File(MEDIA_PATH);     
            songsList = find_files(home);
            if(songsList == null){
            	home = Environment.getExternalStorageDirectory();
            	getMusicDirectory(home);
            	home = new File(MEDIA_PATH2);
            	songsList = find_files(home);
            }
            
            // return songs list array
            return songsList;
	}

	public String getMusicDirectory(File root){
		
		String retPath = "";
		
		String[] paths = root.list();
		
		
		return retPath;
	}
	
        private ArrayList<HashMap<String, String>> find_files(File root)
        {
            try{
                File[] files = root.listFiles(); 
                Random r = new Random();
                String[] locations = {"other", "home", "work"};
                for (File file : files) {
                    if (file.isFile()) {
                        if(file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3")) {
                            HashMap<String, String> song = new HashMap<String, String>();
                            if (file.getAbsolutePath().matches("(.*)\\/running\\/(.*)")) {
                                song.put("activity", "Running");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/foot\\/(.*)")) {
                                song.put("activity", "Foot");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/still\\/(.*)")) {
                                song.put("activity", "Still");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/tilting\\/(.*)")) {
                                song.put("activity", "Tilting");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/vehicle\\/(.*)")) {
                                song.put("activity", "Vehicle");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/cycling\\/(.*)")) {
                                song.put("activity", "Cycling");
                            }
                            else {
                                song.put("activity", "default");
                            }
                            song.put("location", locations[r.nextInt(locations.length)]);
                            song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                            song.put("songPath", file.getAbsolutePath());
                            // Adding each song to SongList
                            songsList.add(song);
                        }
                    } else if (file.isDirectory()) {
                        find_files(file);
                    }
                }
            }
            catch (Exception e) {
                return null;
            }
            return songsList;
        }

        public void updateFilteredList(String activity, double lat, double lon) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            currentLat = lat;
            currentLon = lon;
            currentCustomLocation = checkLocation();
            for(int i = 0; i < songsList.size(); i++) {
                HashMap<String, String> song = songsList.get(i);
                if(song.get("activity").equals(activity) && song.get("location").equals(currentCustomLocation)) {
                    list.add(i);
                }
            }
            if(list.size() == 0) {
                for(int i=0; i<songsList.size();i++) {
                    list.add(i);
                }
            }
            filteredList = list;
        }
        
        public String checkLocation() {
            double dist1 = Haversine.haversine(currentLat, currentLon, lat1, lon1);
            double dist2 = Haversine.haversine(currentLat, currentLon, lat2, lon2);
            if(dist1 < LIMIT) {
                return "home";
            } else if(dist2 < LIMIT) {
                return "work";
            } else {
                return "other";
            }
        }

	public ArrayList<Integer> getFilteredList() {
            return filteredList;
        }
	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
