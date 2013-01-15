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
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements OnItemClickListener{

    public static final String[] from = {"song", "artist", "duration", "url", "aid", "oid"};
    private static final String TAG = SearchActivity.class.getName();

	private EditText searchText;
	private ListView searchList;

    private VKApiService apiService;
    private boolean mBound = false;

    private MyDownloadManager myDownloadManager;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            VKApiService.VKApiBinder binder = (VKApiService.VKApiBinder) service;
            apiService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        searchList = (ListView)findViewById(R.id.searchResultsList);
        View header = this.getLayoutInflater().inflate(R.layout.search_list_header, searchList, false);
        searchList.addHeaderView(header);
        searchList.setAdapter(null);
        ((TextView)findViewById(R.id.header_title)).setText(this.getResources().getQuantityString(R.plurals.number_of_found_audio_items, 0, 0));
        searchList.setOnItemClickListener(this);
        
        searchText = (EditText)findViewById(R.id.searchText);
        registerForContextMenu(searchList);

        myDownloadManager = new MyDownloadManager(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Map<String, String> data = null;
        switch (item.getItemId()) {
            case R.id.search_add_context_menu:

                data = (Map<String, String>)searchList.getAdapter().getItem(info.position);

                final String aid = data.get(from[4]);
                final String oid = data.get(from[5]);
                final String song = data.get(from[0]);
                final String artist = data.get(from[1]);

                (new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... objects) {
                        apiService.addAudio(aid, oid);

                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object response) {
                        Toast.makeText(SearchActivity.this, song + " - " + artist + " was added", Toast.LENGTH_SHORT).show();
                    }
                }).execute();

                return true;
            case R.id.search_download_context_menu:

                data = (Map<String, String>)searchList.getAdapter().getItem(info.position);

                String uri = data.get(from[3]);
                String what = data.get(from[1]) + " - " + data.get(from[0]);

                myDownloadManager.download(uri, what);

                return true;
            case R.id.search_play_context_menu:

                data = (Map<String, String>)searchList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(from[0], data.get(from[0]));
                intent.putExtra(from[1], data.get(from[1]));
                intent.putExtra(from[3], data.get(from[3]));

                startService(intent);
                startActivity(new Intent(this, MusicControlActivity.class));

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, VKApiService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    public void onSearchButtonClick(View v){
        Log.d(TAG, "onSearchButtonClick starts...");
    	String q = searchText.getText().toString();
    	if (q != null && !q.equals("")){
            Log.d(TAG, "onSearchButtonClick: running search task...");
    		new LoadSearchResultsTask().execute(q);
    	}
    }
    
    private class LoadSearchResultsTask extends AsyncTask<String, Void, VKAudioSearchResponse> {
    	
		@Override
		protected VKAudioSearchResponse doInBackground(String... arg0) {
			VKAudioSearchResponse response = null;
			
			try {
				response = apiService.searchAudio(arg0[0], "1", "2", "0", 10, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return response;
		}
    	@Override
    	protected void onPostExecute(VKAudioSearchResponse response) {

            Log.d(TAG, "onPostExecute: number of results = " + response.getItems().size());

    		int[] to = {R.id.song, R.id.artist, R.id.duration};
    		
    		ArrayList<Map<String, Object>> data = new ArrayList<Map<String,Object>>(response.getItems().size());
    		
    		for (int i = 0; i < response.getItems().size(); i++) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			
    			map.put(from[0], response.getItems().get(i).title);
    			map.put(from[1], response.getItems().get(i).artist);
    			map.put(from[2], response.getItems().get(i).duration);
    			map.put(from[3], response.getItems().get(i).url);
                map.put(from[4], response.getItems().get(i).aid);
                map.put(from[5], response.getItems().get(i).owner_id);
    			
    			data.add(map);
    		}
    		
    		SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this, data, R.layout.audio_item, from, to);
    		searchList.setAdapter(adapter);
    	}
    }
    
    public void onItemClick(AdapterView<?> list, View parent, int pos, long id) {
        Map<String, String> data = (Map<String, String>)searchList.getAdapter().getItem(pos);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(from[0], data.get(from[0]));
        intent.putExtra(from[1], data.get(from[1]));
        intent.putExtra(from[3], data.get(from[3]));

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }
}
