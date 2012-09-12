package com.iradetskiy.vkaudioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;

public class PlayAudioService extends Service implements OnPreparedListener {

	public static final String ACTION_PLAY = "com.iradetskiy.action.PLAY";
	public static final String ACTION_STOP = "com.iradetskiy.action.STOP";
	public static final String ACTION_PAUSE = "com.iradetskiy.action.PAUSE";
	
	MediaPlayer mMediaPlayer = null;
	String currentUrl = null;
	
	@Override
    public void onCreate() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
        mMediaPlayer.setOnPreparedListener(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	if (intent.getAction().equals(PlayAudioService.ACTION_PLAY)){
            
    		if (currentUrl != null) {
    			
    			if (currentUrl.equals(intent.getExtras().getString(SearchActivity.from[3]))) {
    				
    				mMediaPlayer.start();
    			}
    			else {
    				
    				try{
    	            	mMediaPlayer.setDataSource(intent.getExtras().getString(SearchActivity.from[3]));
    	            	mMediaPlayer.prepareAsync();
    	            }
    				catch (Exception e) {}
    			}
    		}            
    	} 
    	else if (intent.getAction().equals(PlayAudioService.ACTION_PAUSE)){
    		
    		mMediaPlayer.pause();    		
    	}
    	else if (intent.getAction().equals(PlayAudioService.ACTION_STOP)){
    		
    		mMediaPlayer.stop();
    	}
    	
		return super.onStartCommand(intent, flags, startId);
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public void onDestroy() {
		
		mMediaPlayer.stop();
		mMediaPlayer.release();
		mMediaPlayer = null;
		
    }
}
