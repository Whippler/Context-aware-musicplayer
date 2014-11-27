package com.androidhive.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("/storage/");
	final static String MEDIA_PATH2 = new String("/storage/SD card/Media/Music/Arch Enemy");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	// Constructor
	public SongsManager(){
		
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
	
	private String getMusicDirectory(File root){
		
		String retPath = "";
		
		String[] paths = root.list();
		
		
		return retPath;
	}
	
        private ArrayList<HashMap<String, String>> find_files(File root)
        {
        	try{
	            File[] files = root.listFiles();
	            for (File file : files) {
	                if (file.isFile()) {
	                    if(file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3")) {
	                        HashMap<String, String> song = new HashMap<String, String>();
	                        song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
	                        song.put("songPath", file.getAbsolutePath());
	                        
	                        // Adding each song to SongList
	                        songsList.add(song);
	                    }
	                } else if (file.isDirectory()) {
	                    find_files(file);
	                }
	            }
	            return songsList;
        	}
        	catch(Exception e){
        		return null;
        	}
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
