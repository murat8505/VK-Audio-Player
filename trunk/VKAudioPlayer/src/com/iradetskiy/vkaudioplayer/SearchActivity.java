package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements OnItemClickListener{

	private final String accessToken = "e27affe5e2da0ecbe2da0ecbe6e2f0a839ee2dae2db0ec380fa798a1438b154";
	private EditText searchText;
	private ListView searchList;
	private VKAudio api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        searchList = (ListView)findViewById(R.id.searchResultsList);
        searchList.setOnItemClickListener(this);
        
        searchText = (EditText)findViewById(R.id.searchText);
        api = new VKAudio(accessToken);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }
    
    public void onSearchButtonClick(View v){
    	
    }
    
    private class LoadSearchResultsTask extends AsyncTask<String, Void, VKAudioSearchResponse> {
    	
		@Override
		protected VKAudioSearchResponse doInBackground(String... arg0) {
			VKAudioSearchResponse response = null;
			
			try {
				response = api.search(arg0[0], "1", "2", "0", 10, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return response;
		}
    	@Override
    	protected void onPostExecute(VKAudioSearchResponse response) {
    		
    		String[] from = {"song", "artist", "duration", "url"};
    		int[] to = {R.id.song, R.id.artist, R.id.duration};
    		
    		ArrayList<Map<String, Object>> data = new ArrayList<Map<String,Object>>(response.getItems().size());
    		
    		for (int i = 0; i < response.getItems().size(); i++) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			
    			map.put(from[0], response.getItems().get(i).title);
    			map.put(from[1], response.getItems().get(i).artist);
    			map.put(from[2], response.getItems().get(i).duration);
    			map.put(from[3], response.getItems().get(i).url);
    			
    			data.add(map);
    		}
    		
    		SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this, data, R.layout.audio_item, from, to);
    		searchList.setAdapter(adapter);
    	}
    }
    
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    	
	}
}
