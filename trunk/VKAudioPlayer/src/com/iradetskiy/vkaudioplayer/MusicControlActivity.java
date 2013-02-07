package com.iradetskiy.vkaudioplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iradetskiy.utility.TimeUtility;
import com.iradetskiy.vkapi.VKAudioItem;

public class MusicControlActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private boolean isPlaying = true;
    private SeekBar seekBar;
    private boolean killFlag = false;
    public static final String tag = "com.example.MusicControlActivity";
    private NotificationManager manager;

    private TextView song;
    private TextView artist;
    private TextView currentPosition;
    private TextView duration;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_control);

        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(1000);

        song = (TextView)findViewById(R.id.song);
        artist = (TextView)findViewById(R.id.artist);
        currentPosition = (TextView)findViewById(R.id.current_position);
        duration = (TextView)findViewById(R.id.duration);

        Intent intent = getIntent();
        if (intent != null) {
            isPlaying = intent.getBooleanExtra("is_playing", true);
            ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
            playButton.setImageResource(isPlaying ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
            song.setText(intent.getStringExtra(VKAudioItem.TITLE));
            artist.setText(intent.getStringExtra(VKAudioItem.ARTIST));
            currentPosition.setText(intent.getStringExtra("position"));
            duration.setText(intent.getStringExtra("duration"));
        }

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(tag, 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayMusicService.ACTION_SEEK_TO);
        filter.addAction(PlayMusicService.ACTION_BUFFERING_UPDATE);
        registerReceiver(seekReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_control_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kill_player:

                killFlag = true;
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPlayPressed(View v) {
        if (isPlaying) {
            ((ImageButton)v).setImageResource(android.R.drawable.ic_media_pause);
            startService(new Intent(PlayMusicService.ACTION_PAUSE));
        }
        else {
            ((ImageButton)v).setImageResource(android.R.drawable.ic_media_play);
            Intent i = new Intent(PlayMusicService.ACTION_PLAY);
            i.putExtra(VKAudioItem.TITLE, song.getText());
            i.putExtra(VKAudioItem.ARTIST, artist.getText());
            startService(i);//new Intent(PlayMusicService.ACTION_PLAY));
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

        if (!killFlag) {
            Notification notification = new Notification((isPlaying) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause,
                    song.getText(), System.currentTimeMillis());
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            Intent reinvokeIntent = new Intent(this, MusicControlActivity.class);
            reinvokeIntent.putExtra("is_playing", isPlaying);
            reinvokeIntent.putExtra(VKAudioItem.TITLE, song.getText());
            reinvokeIntent.putExtra(VKAudioItem.ARTIST, artist.getText());
            reinvokeIntent.putExtra("position", currentPosition.getText());
            reinvokeIntent.putExtra("duration", duration.getText());

            Log.d(tag, "isPlaying when destroy = " + isPlaying);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, reinvokeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(this, song.getText(), artist.getText(), pendingIntent);
            manager.notify(tag, 0, notification);
        }
        else {
            startService(new Intent(PlayMusicService.ACTION_KILL));
        }

        unregisterReceiver(seekReceiver);

        /*unregisterReceiver(seekReceiver);
        startService(new Intent(PlayMusicService.ACTION_KILL));
        Log.d("MusicControlActivity", "Activity destroyed");   */
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
                int currentPosition = intent.getIntExtra("current_position", -1);
                if (seekTo != -1) {
                    seekBar.setProgress(seekTo);

                    MusicControlActivity.this.currentPosition.setText(TimeUtility.formatSeconds(currentPosition + "")/*String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))
                    )*/);
                    MusicControlActivity.this.artist.setText(intent.getStringExtra("artist"));
                    MusicControlActivity.this.song.setText(intent.getStringExtra("song"));
                    int duration = intent.getIntExtra("duration", -1);
                    MusicControlActivity.this.duration.setText(TimeUtility.formatSeconds(duration + "")/*String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    )*/);
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