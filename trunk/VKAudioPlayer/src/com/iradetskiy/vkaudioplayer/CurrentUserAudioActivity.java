package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CurrentUserAudioActivity extends Activity implements AdapterView.OnItemClickListener{

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Map<String, String> data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(i);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(from[0], data.get(from[0]));
        intent.putExtra(from[1], data.get(from[1]));
        intent.putExtra(from[3], data.get(from[3]));

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }

    public final static String TAG = CurrentUserAudioActivity.class.getName();

	public static final String[] from = {"song", "artist", "duration", "url", "aid", "oid"};
	ListView currentUserAudioList;

    private VKApiService apiService;
    private boolean mBound = false;
    private String accessToken;
    private String userId;
    private MyDownloadManager myDownloadManager;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            VKApiService.VKApiBinder binder = (VKApiService.VKApiBinder) service;
            apiService = binder.getService();
            mBound = true;

            CurrentUserAudioActivity.this.onApiConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
		setContentView(R.layout.current_user_audio);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		userId = (String) extras.get(VKApi.USER_ID);
		accessToken = (String) extras.get(VKApi.ACCESS_TOKEN);

		currentUserAudioList = (ListView)findViewById(R.id.currentUserAudioList);
        currentUserAudioList.setOnItemClickListener(this);
		this.registerForContextMenu(currentUserAudioList);

        myDownloadManager = new MyDownloadManager(this);
	}

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: accessToken = " + accessToken);

        if (!mBound) {
            Intent intent = new Intent(this, VKApiService.class);
            intent.putExtra(VKApiService.ACCESS_TOKEN, accessToken);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }

    }

    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.audio_item_context_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.current_user_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Map<String, String> data = null;
        switch (item.getItemId()) {
            case R.id.play_context_menu:
                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(from[0], data.get(from[0]));
                intent.putExtra(from[1], data.get(from[1]));
                intent.putExtra(from[3], data.get(from[3]));

                startService(intent);
                startActivity(new Intent(this, MusicControlActivity.class));
                return true;
            case R.id.download_context_menu:

                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                String uri = data.get(from[3]);
                String what = data.get(from[1]) + " - " + data.get(from[0]);

                myDownloadManager.download(uri, what);
                return true;
            case R.id.remove_menu:
                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                final String aid = data.get(from[4]);
                final String song = data.get(from[0]);
                final String artist = data.get(from[1]);

                (new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        apiService.deleteAudio(aid, userId);
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                    @Override
                    protected void onPostExecute(Object response) {
                        Toast.makeText(CurrentUserAudioActivity.this, song + " - " + artist + " was removed", Toast.LENGTH_SHORT).show();
                    }
                }).execute();

                new LoadAudioGetResultsTask().execute(userId);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.logout_menu:
                CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();

                this.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private class LoadAudioGetResultsTask extends
			AsyncTask<String, Void, VKAudioGetResponse> {

		@Override
		protected VKAudioGetResponse doInBackground(String... arg0) {
			VKAudioGetResponse response = null;

			try {
				response = apiService.getAudio("", "", "", "", "", "", "");//mApi.getAudio("", "", "", "", "", "", "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(VKAudioGetResponse response) {

			int[] to = { R.id.song, R.id.artist, R.id.duration };

			ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
					response.getItems().size());

			for (int i = 0; i < response.getItems().size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(from[0], response.getItems().get(i).title);
				map.put(from[1], response.getItems().get(i).artist);
				map.put(from[2], response.getItems().get(i).duration);
				map.put(from[3], response.getItems().get(i).url);
                map.put(from[4], response.getItems().get(i).aid);


				data.add(map);
			}

			SimpleAdapter adapter = new SimpleAdapter(CurrentUserAudioActivity.this,
					data, R.layout.audio_item, from, to);
            //adapter.notifyDataSetChanged();
			currentUserAudioList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
		}
	}

	private class LoadUserNameTask extends
			AsyncTask<String, Void, VKUsersGetResponse> {

		@Override
		protected VKUsersGetResponse doInBackground(String... arg0) {
			VKUsersGetResponse response = null;

			try {
				response = apiService
						.getUsers(arg0[0], "first_name,last_name", "nom");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(VKUsersGetResponse response) {

            Log.d(TAG, "Setting the title: " + response.getResults().get(0).first_name + " "
                    + response.getResults().get(0).last_name);

			CurrentUserAudioActivity.this
					.setTitle(response.getResults().get(0).first_name + " "
							+ response.getResults().get(0).last_name);
		}
	}

    private void onApiConnected() {
        new LoadUserNameTask().execute(userId);
        new LoadAudioGetResultsTask().execute(userId);
    }
}
