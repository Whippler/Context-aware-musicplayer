package com.androidhive.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("/storage/sdcard0/");
	final static String MEDIA_PATH2 = new String("/storage/SD card/Media/Music/Arch Enemy");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private ArrayList<Integer> filteredList = new ArrayList<Integer>();
	
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

	public String getMusicDirectory(File root){
		
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
                            if (file.getAbsolutePath().matches("(.*)\\/running\\/(.*)")) {
                                song.put("activity", "Running");
                            }
                            else if(file.getAbsolutePath().matches("(.*)\\/foot\\/(.*)")) {
                                song.put("activity", "On foot");
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

        public void updateFilteredList(String activity) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for(int i = 0; i < songsList.size(); i++) {
                HashMap<String, String> song = songsList.get(i);
                if(song.get("activity").equals(activity)) {
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
