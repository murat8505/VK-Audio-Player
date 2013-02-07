package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.iradetskiy.utility.DownloadUtility;
import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioItem;
import com.iradetskiy.vkapi.VKAudioSearchResponse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements OnItemClickListener{
	
    private static final String TAG = SearchActivity.class.getName();

	private EditText searchText;
	private ListView searchList;
    private VKApi mApi;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        searchList = (ListView)findViewById(R.id.searchResultsList);
        searchList.setAdapter(null);
        searchList.setOnItemClickListener(this);
        
        searchText = (EditText)findViewById(R.id.searchText);
        registerForContextMenu(searchList);
        mApi = VKApi.getApi(null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_context_menu, menu);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Map<String, String> data = null;
        switch (item.getItemId()) {
            case R.id.search_add_context_menu:

                data = (Map<String, String>)searchList.getAdapter().getItem(info.position);

                final String aid = data.get(VKAudioItem.AID);
                final String oid = data.get(VKAudioItem.OID);
                final String song = data.get(VKAudioItem.TITLE);
                final String artist = data.get(VKAudioItem.ARTIST);

                (new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... objects) {
                        mApi.addAudio(aid, oid);

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

                String uri = data.get(VKAudioItem.URL);
                String what = data.get(VKAudioItem.ARTIST) + " - " + data.get(VKAudioItem.TITLE);

                DownloadUtility.getDownloadUtility(this).download(uri, what);

                return true;
            case R.id.search_play_context_menu:

                data = (Map<String, String>)searchList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(VKAudioItem.TITLE, data.get(VKAudioItem.TITLE));
                intent.putExtra(VKAudioItem.ARTIST, data.get(VKAudioItem.ARTIST));
                intent.putExtra(VKAudioItem.URL, data.get(VKAudioItem.URL));

                startService(intent);
                startActivity(new Intent(this, MusicControlActivity.class));

                return true;
            default:
                return super.onContextItemSelected(item);
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
				response = mApi.searchAudio(arg0[0], "1", "2", "0", 10, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return response;
		}
    	@Override
    	protected void onPostExecute(VKAudioSearchResponse response) {
    		ArrayList<Map<String, Object>> data = new ArrayList<Map<String,Object>>(response.getItems().size());
    		
    		for (int i = 0; i < response.getItems().size(); i++) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			
    			map.put(VKAudioItem.TITLE, response.getItems().get(i).title);
    			map.put(VKAudioItem.ARTIST, response.getItems().get(i).artist);
    			map.put(VKAudioItem.DURATION, response.getItems().get(i).duration);
    			map.put(VKAudioItem.URL, response.getItems().get(i).url);
                map.put(VKAudioItem.AID, response.getItems().get(i).aid);
                map.put(VKAudioItem.OID, response.getItems().get(i).owner_id);
    			
    			data.add(map);
    		}
    		
    		SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this, data, R.layout.audio_item, 
    				new String[] { VKAudioItem.TITLE, VKAudioItem.ARTIST, VKAudioItem.DURATION }, 
    				new int[] {R.id.song, R.id.artist, R.id.duration});
    		searchList.setAdapter(adapter);
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> list, View parent, int pos, long id) {
        Map<String, String> data = (Map<String, String>)searchList.getAdapter().getItem(pos);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(VKAudioItem.TITLE, data.get(VKAudioItem.TITLE));
        intent.putExtra(VKAudioItem.ARTIST, data.get(VKAudioItem.ARTIST));
        intent.putExtra(VKAudioItem.URL, data.get(VKAudioItem.URL));

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }
}
