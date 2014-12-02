package com.androidhive.musicplayer;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.widget.Toast;

public class NoiseDetector {
	
	private Context context;
	private MediaRecorder mediaRecorder; 
	
	public NoiseDetector(Context context){
		this.context = context;
		initialize();
	}

	private void initialize(){
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile("/dev/null"); //To avoid storing the audio and process it live
	}
	
	public void startRecording(){
		//this or erase the line of meadiaRecorder to null when it stops
		if(mediaRecorder==null){
			initialize();
		}
		
		try {
			mediaRecorder.prepare();
			
			mediaRecorder.start();
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopRecording(){
                try{
                    if(mediaRecorder!=null){
                            mediaRecorder.stop();       
                            mediaRecorder.release();
                            mediaRecorder = null;
                    }
                } 
                catch (IllegalStateException e){
                }
	}
	
	public double getAmplitude(){
		double retVal = 0;
		if (mediaRecorder != null){
			retVal =  mediaRecorder.getMaxAmplitude();
			retVal = 20 * Math.log10(retVal/ 2700.0);
		}
		
		return retVal;
	}
	
	public void modifyVolume(AudioManager audioManager){
		Toast.makeText(context, "Noise Thersold Crossed", Toast.LENGTH_SHORT).show();
		
		int actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(actualVolume <= audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)-10){
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, actualVolume+1, AudioManager.FLAG_VIBRATE);
		}
	}
}
