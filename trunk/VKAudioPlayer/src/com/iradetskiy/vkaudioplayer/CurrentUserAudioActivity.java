package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.iradetskiy.utility.DownloadUtility;
import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioGetResponse;
import com.iradetskiy.vkapi.VKAudioItem;
import com.iradetskiy.vkapi.VKUsersGetResponse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CurrentUserAudioActivity extends Activity implements AdapterView.OnItemClickListener{

    @SuppressWarnings("unchecked")
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Map<String, String> data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(i);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(VKAudioItem.TITLE, data.get(VKAudioItem.TITLE));
        intent.putExtra(VKAudioItem.ARTIST, data.get(VKAudioItem.ARTIST));
        intent.putExtra(VKAudioItem.URL, data.get(VKAudioItem.URL));

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }

    public final static String TAG = CurrentUserAudioActivity.class.getName();

	ListView currentUserAudioList;
    private VKApi mApi;
    private String accessToken;
    private String userId;
    private ArrayList<Map<String, String>> mUserAudioList;
    private String mUserName;
	
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
		
		RestoreObject restoreData = (RestoreObject) getLastNonConfigurationInstance();
        if (restoreData != null) {
        	if (restoreData.mUserAudio.size() != 0) {
        		mUserName = restoreData.mUserName;
        		mUserAudioList = restoreData.mUserAudio;
        	
        		SimpleAdapter adapter = new SimpleAdapter(CurrentUserAudioActivity.this, mUserAudioList, R.layout.audio_item, 
    				new String[] { VKAudioItem.TITLE, VKAudioItem.ARTIST, VKAudioItem.DURATION }, 
    				new int[] {R.id.song, R.id.artist, R.id.duration});
        		currentUserAudioList.setAdapter(adapter);
        	}
    		
    		setTitle(String.format(getResources().getString(R.string.current_user_audio_activity_when_loaded), mUserName, mUserAudioList.size()));
        } else {
        	setTitle(getResources().getString(R.string.current_user_audio_activity_when_loading));
        }
		
        mApi = VKApi.getApi(accessToken);
	}

    @Override
    public void onStart() {
        super.onStart();

        new LoadUserNameTask().execute(userId);
        new LoadAudioGetResultsTask().execute(userId);
    }
    
    private class RestoreObject {
    	public String mUserName;
    	public ArrayList<Map<String, String>> mUserAudio;
    	public RestoreObject(String userName, ArrayList<Map<String, String>> userAudio) {
    		mUserName = userName;
    		mUserAudio = userAudio;
    	}
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	
    	final RestoreObject restoreObject = new RestoreObject(mUserName, mUserAudioList);
    	return restoreObject;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @SuppressWarnings("unchecked")
	@Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Map<String, String> data = null;
        switch (item.getItemId()) {
            case R.id.play_context_menu:
                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(VKAudioItem.TITLE, data.get(VKAudioItem.TITLE));
                intent.putExtra(VKAudioItem.ARTIST, data.get(VKAudioItem.ARTIST));
                intent.putExtra(VKAudioItem.URL, data.get(VKAudioItem.URL));

                startService(intent);
                startActivity(new Intent(this, MusicControlActivity.class));
                return true;
            case R.id.download_context_menu:

                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                String uri = data.get(VKAudioItem.URL);
                String what = data.get(VKAudioItem.ARTIST) + " - " + data.get(VKAudioItem.TITLE);

                DownloadUtility.getDownloadUtility(this).download(uri, what);
                
                return true;
            case R.id.remove_menu:
                data = (Map<String, String>)currentUserAudioList.getAdapter().getItem(info.position);

                final String aid = data.get(VKAudioItem.AID);
                final String song = data.get(VKAudioItem.TITLE);
                final String artist = data.get(VKAudioItem.ARTIST);

                (new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        mApi.deleteAudio(aid, userId);
                        return null;  
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
				response = mApi.getAudio("", "", "", "", "", "", "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(VKAudioGetResponse response) {

			ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>(
					response.getItems().size());

			for (int i = 0; i < response.getItems().size(); i++) {
				Map<String, String> map = new HashMap<String, String>();

				map.put(VKAudioItem.TITLE, response.getItems().get(i).title);
				map.put(VKAudioItem.ARTIST, response.getItems().get(i).artist);
				map.put(VKAudioItem.DURATION, response.getItems().get(i).duration);
				map.put(VKAudioItem.URL, response.getItems().get(i).url);
                map.put(VKAudioItem.AID, response.getItems().get(i).aid);


				data.add(map);
			}

			CurrentUserAudioActivity.this.mUserAudioList = data;
			
			SimpleAdapter adapter = new SimpleAdapter(CurrentUserAudioActivity.this,
					data, R.layout.audio_item, 
					new String[] { VKAudioItem.TITLE, VKAudioItem.ARTIST, VKAudioItem.DURATION }, 
					new int[] { R.id.song, R.id.artist, R.id.duration });
			
			currentUserAudioList.setAdapter(adapter);
			
			if (CurrentUserAudioActivity.this.mUserName == null) {
            	CurrentUserAudioActivity.this
					.setTitle(String.format(
							CurrentUserAudioActivity.this.getResources().getString(R.string.current_user_audio_activity_when_audiolist_loaded), 
							CurrentUserAudioActivity.this.mUserAudioList.size()));
            } else {
            	CurrentUserAudioActivity.this
				.setTitle(String.format(
						CurrentUserAudioActivity.this.getResources().getString(R.string.current_user_audio_activity_when_loaded), 
						CurrentUserAudioActivity.this.mUserName, CurrentUserAudioActivity.this.mUserAudioList.size()));
            }
		}
	}

	private class LoadUserNameTask extends
			AsyncTask<String, Void, VKUsersGetResponse> {

		@Override
		protected VKUsersGetResponse doInBackground(String... arg0) {
			VKUsersGetResponse response = null;

			try {
				response = mApi
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

            CurrentUserAudioActivity.this.mUserName = 
            		response.getResults().get(0).first_name + " "
							+ response.getResults().get(0).last_name;
            if (CurrentUserAudioActivity.this.mUserAudioList == null) {
            	CurrentUserAudioActivity.this
					.setTitle(String.format(
							CurrentUserAudioActivity.this.getResources().getString(R.string.current_user_audio_activity_when_username_loaded), 
							CurrentUserAudioActivity.this.mUserName));
            } else {
            	CurrentUserAudioActivity.this
				.setTitle(String.format(
						CurrentUserAudioActivity.this.getResources().getString(R.string.current_user_audio_activity_when_loaded), 
						CurrentUserAudioActivity.this.mUserName, CurrentUserAudioActivity.this.mUserAudioList.size()));
            }	
		}
	}
}
