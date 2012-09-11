package com.example.a2dptest;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PlayAudioService extends Service implements OnPreparedListener {

	private static final String ACTION_PLAY = "com.example.action.PLAY";
	MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.i("iradetskiy", this.toString());
    	if (intent.getAction().equals("com.iradetskiy.play")){
    		
    		/*Uri myUri = Uri.fromFile(new File("/sdcard/Music/Kalimba.mp3"));
            mMediaPlayer = new MediaPlayer();
    		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // initialize it here
            mMediaPlayer.setOnPreparedListener(this);
            try{
            	mMediaPlayer.setDataSource(this, myUri);
            	mMediaPlayer.prepareAsync();
            }
			catch (Exception e){
				
			}*/
    	}
    	if (intent.getAction().equals("com.iradetskiy.stop")){
    		/*mMediaPlayer.stop();
    		mMediaPlayer.release();
    		mMediaPlayer = null;
    		stopSelf();*/
    	}
        //if (intent.getAction().equals(ACTION_PLAY)) {
        	/*Uri myUri = Uri.fromFile(new File("/sdcard/Music/Kalimba.mp3"));
            mMediaPlayer = new MediaPlayer();
    		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // initialize it here
            mMediaPlayer.setOnPreparedListener(this);
            try{
            	mMediaPlayer.setDataSource(this, myUri);
            	mMediaPlayer.prepareAsync();
            }
			catch (Exception e){
				
			}*/
        //S}
		return super.onStartCommand(intent, flags, startId);
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
