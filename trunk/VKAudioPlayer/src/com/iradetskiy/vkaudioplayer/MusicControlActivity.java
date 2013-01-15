package com.iradetskiy.vkaudioplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MusicControlActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private boolean isPlaying = true;
    private SeekBar seekBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_control);

        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(1000);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayMusicService.ACTION_SEEK_TO);
        filter.addAction(PlayMusicService.ACTION_BUFFERING_UPDATE);
        registerReceiver(seekReceiver, filter);
    }

    public void onPlayPressed(View v) {
        if (isPlaying) {
            ((ImageButton)v).setImageResource(android.R.drawable.ic_media_pause);
            startService(new Intent(PlayMusicService.ACTION_PAUSE));
        }
        else {
            ((ImageButton)v).setImageResource(android.R.drawable.ic_media_play);
            startService(new Intent(PlayMusicService.ACTION_PLAY));
        }
        isPlaying = !isPlaying;
    }

    public void onPrevPressed(View v) {

    }

    public void onNextPressed(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(seekReceiver);
        startService(new Intent(PlayMusicService.ACTION_KILL));
        Log.d("MusicControlActivity", "Activity destroyed");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            Intent intent = new Intent();
            intent.setAction(PlayMusicService.ACTION_SEEK_TO);
            intent.putExtra("seek_to", i);

            startService(intent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private BroadcastReceiver seekReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayMusicService.ACTION_SEEK_TO)) {
                int seekTo = intent.getIntExtra("seek_to", -1);
                if (seekTo != -1) {
                    seekBar.setProgress(seekTo);
                }
            }
            else if (action.equals(PlayMusicService.ACTION_BUFFERING_UPDATE)) {
                int secondaryProgress = intent.getIntExtra("buffering_update", -1);
                if (secondaryProgress != -1) {
                    seekBar.setSecondaryProgress(secondaryProgress * 10);
                }
            }
        }
    };
}