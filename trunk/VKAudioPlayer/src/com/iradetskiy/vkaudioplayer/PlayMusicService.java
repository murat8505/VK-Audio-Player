package com.iradetskiy.vkaudioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import com.iradetskiy.vkapi.VKAudioItem;


public class PlayMusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener{

    public static final String ACTION_PLAY = "com.example.PlayMusicService.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.PlayMusicService.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.example.PlayMusicService.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.PlayMusicService.ACTION_PREVIOUS";
    public static final String ACTION_KILL = "com.example.PlayMusicService.ACTION_KILL";
    public static final String ACTION_SEEK_TO = "com.example.PlayMusicService.ACTION_SEEK_TO";
    public static final String ACTION_BUFFERING_UPDATE = "com.example.PlayMusicService.ACTION_BUFFERING_UPDATE";

    private boolean needToNotify = false;
    private MediaPlayer mediaPlayer;

    private String currentSong;
    private String currentArtist;

    private Thread musicControlNotifier = new Thread() {
        @Override
        public void run() {
            while(needToNotify) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_SEEK_TO);
                        intent.putExtra("seek_to", (mediaPlayer.getCurrentPosition() * 1000) / mediaPlayer.getDuration());
                        intent.putExtra("current_position", mediaPlayer.getCurrentPosition());
                        intent.putExtra("artist", currentArtist);
                        intent.putExtra("song", currentSong);
                        intent.putExtra("duration", mediaPlayer.getDuration());
                        sendBroadcast(intent);
                    }
                    sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("PlayMusicService", "sleep error");
                }
            }
        }
    };

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        needToNotify = true;
        musicControlNotifier.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (action.equals(ACTION_PLAY)) {

            String uri = intent.getStringExtra(VKAudioItem.URL);
            currentSong = intent.getStringExtra(VKAudioItem.TITLE);
            currentArtist = intent.getStringExtra(VKAudioItem.ARTIST);

            if (uri != null) {

                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(uri));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.d("PlayMusicService", "Trouble with playing");
                }
            }
            else {
                mediaPlayer.start();
            }
        }
        else if (action.equals(ACTION_PAUSE)) {
            mediaPlayer.pause();
        }
        else if (action.equals(ACTION_KILL)) {
            stopSelf();
        }
        else if (action.equals(ACTION_SEEK_TO)) {
            int seekTo = intent.getIntExtra("seek_to", -1);
            if (seekTo != -1) {
                int duration = mediaPlayer.getDuration();

                mediaPlayer.seekTo((seekTo * duration) / 1000);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        needToNotify = false;
        Toast.makeText(this, "PlayMusicService killed", Toast.LENGTH_SHORT).show();
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("PlayMusicService", "Audio prepared...");
        mediaPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Intent intent = new Intent();
        intent.setAction(ACTION_BUFFERING_UPDATE);
        intent.putExtra("buffering_update", i);
        sendBroadcast(intent);
    }
}
