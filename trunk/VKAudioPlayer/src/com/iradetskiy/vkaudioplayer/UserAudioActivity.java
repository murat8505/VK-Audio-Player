package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.Map;

import com.iradetskiy.utility.DownloadUtility;
import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioGetRequest;
import com.iradetskiy.vkapi.VKAudioGetResponse;
import com.iradetskiy.vkapi.VKAudioItem;
import com.iradetskiy.vkapi.VKGetUsersRequest;
import com.iradetskiy.vkapi.VKUsersGetResponse;
import com.iradetskiy.vkaudioplayer.adapter.UserAudioAdapter;
import com.iradetskiy.vkaudioplayer.task.GetAudioTask;
import com.iradetskiy.vkaudioplayer.task.GetUsersTask;
import com.iradetskiy.vkaudioplayer.task.OnGetAudioResponseListener;

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

public class UserAudioActivity extends Activity 
	implements AdapterView.OnItemClickListener, OnGetAudioResponseListener, GetUsersTask.OnGetUsersListener{

    public final static String TAG = UserAudioActivity.class.getName();

	ListView currentUserAudioList;
    private VKApi mApi;
    private ArrayList<Map<String, String>> mUserAudioList;
    private String mUserName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
		setContentView(R.layout.current_user_audio);

		currentUserAudioList = (ListView)findViewById(R.id.currentUserAudioList);
        currentUserAudioList.setOnItemClickListener(this);
		this.registerForContextMenu(currentUserAudioList);
		
		RestoreObject restoreData = (RestoreObject) getLastNonConfigurationInstance();
        if (restoreData != null) {
        	if (restoreData.mUserAudio.size() != 0) {
        		mUserName = restoreData.mUserName;
        		mUserAudioList = restoreData.mUserAudio;
        	
        		SimpleAdapter adapter = new SimpleAdapter(UserAudioActivity.this, mUserAudioList, R.layout.audio_item, 
    				new String[] { VKAudioItem.TITLE, VKAudioItem.ARTIST, VKAudioItem.DURATION }, 
    				new int[] {R.id.song, R.id.artist, R.id.duration});
        		currentUserAudioList.setAdapter(adapter);
        	}
    		
    		setTitle(String.format(getResources().getString(R.string.current_user_audio_activity_when_loaded), mUserName, mUserAudioList.size()));
        } else {
        	setTitle(getResources().getString(R.string.current_user_audio_activity_when_loading));
        }
		
        mApi = VKApi.getApi();
        
        if (mApi != null) {
        	
        }
	}

    @Override
    public void onStart() {
        super.onStart();
        
        GetUsersTask getUsersTask = new GetUsersTask();
        getUsersTask.setOnGetUsersListener(this);
        getUsersTask.execute(new VKGetUsersRequest(mApi.getUserId()));
        
        GetAudioTask getAudioTask = new GetAudioTask();
        getAudioTask.setOnGetAudioResponseListener(this);
        getAudioTask.execute(new VKAudioGetRequest(mApi.getUserId()));
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

	@Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        VKAudioItem data = null;
        switch (item.getItemId()) {
            case R.id.play_context_menu:
                data = (VKAudioItem)currentUserAudioList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(VKAudioItem.TITLE, data.title);
                intent.putExtra(VKAudioItem.ARTIST, data.artist);
                intent.putExtra(VKAudioItem.URL, data.url);

                startService(intent);
                startActivity(new Intent(this, MusicControlActivity.class));
                return true;
            case R.id.download_context_menu:

                data = (VKAudioItem)currentUserAudioList.getAdapter().getItem(info.position);

                String uri = data.url;
                String what = data.artist + " - " + data.title;

                DownloadUtility.getDownloadUtility(this).download(uri, what);
                
                return true;
            case R.id.remove_menu:
                data = (VKAudioItem)currentUserAudioList.getAdapter().getItem(info.position);

                final String aid = data.aid;
                final String song = data.title;
                final String artist = data.artist;

                (new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        mApi.deleteAudio(aid, mApi.getUserId());
                        return null;  
                    }
                    @Override
                    protected void onPostExecute(Object response) {
                        Toast.makeText(UserAudioActivity.this, song + " - " + artist + " was removed", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
                
                GetAudioTask getAudioTask = new GetAudioTask();
                getAudioTask.setOnGetAudioResponseListener(this);
                getAudioTask.execute(new VKAudioGetRequest(mApi.getUserId()));

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
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        VKAudioItem data = (VKAudioItem)currentUserAudioList.getAdapter().getItem(i);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(VKAudioItem.TITLE, data.title);
        intent.putExtra(VKAudioItem.ARTIST, data.artist);
        intent.putExtra(VKAudioItem.URL, data.url);

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }

	@Override
	public void onGetAudioResponse(VKAudioGetResponse response) {
		UserAudioAdapter adapter = new UserAudioAdapter(UserAudioActivity.this, response);
		currentUserAudioList.setAdapter(adapter);
	}

	@Override
	public void onGetUsersResponse(VKUsersGetResponse response) {
		UserAudioActivity.this.mUserName = response.getResults().get(0).first_name + " "
				+ response.getResults().get(0).last_name;
		
		UserAudioActivity.this
		.setTitle(String.format(
				UserAudioActivity.this.getResources().getString(R.string.current_user_audio_activity_when_username_loaded), 
				UserAudioActivity.this.mUserName));
	}
}
