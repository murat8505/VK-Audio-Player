package com.iradetskiy.vkaudioplayer;

import com.iradetskiy.utility.DownloadUtility;
import com.iradetskiy.vkapi.VKAudioItem;
import com.iradetskiy.vkapi.VKAudioSearchRequest;
import com.iradetskiy.vkapi.VKAudioSearchResponse;
import com.iradetskiy.vkaudioplayer.adapter.SearchAdapter;
import com.iradetskiy.vkaudioplayer.task.AddAudioTask;
import com.iradetskiy.vkaudioplayer.task.SearchAudioTask;
import com.iradetskiy.vkaudioplayer.task.SearchAudioTask.OnSearchAudioResponseListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends Activity implements OnItemClickListener, OnSearchAudioResponseListener{
	
    private static final String TAG = SearchActivity.class.getName();

	private EditText searchText;
	private ListView searchList;
    private VKAudioSearchResponse restoreData;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        searchText = (EditText)findViewById(R.id.searchText);
        searchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId){
				case EditorInfo.IME_ACTION_SEARCH:
					onSearchButtonClick(null);
					return true;
				default:
					return false;	
				}
			}
		});
        
        searchList = (ListView)findViewById(R.id.searchResultsList);
        searchList.setAdapter(null);
        searchList.setOnItemClickListener(this);
        
        restoreData = (VKAudioSearchResponse) getLastNonConfigurationInstance();
        if (restoreData != null && restoreData.getCount() != 0) {
    		searchList.setAdapter(new SearchAdapter(this, restoreData));
        }
        
        registerForContextMenu(searchList);
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
        VKAudioItem data = null;
        switch (item.getItemId()) {
            case R.id.search_add_context_menu:

            	AddAudioTask addAudio = new AddAudioTask();
            	addAudio.execute((VKAudioItem)searchList.getAdapter().getItem(info.position));
            	
                return true;
            case R.id.search_download_context_menu:

                data = (VKAudioItem)searchList.getAdapter().getItem(info.position);

                String uri = data.url;
                String what = data.artist + " - " + data.title;

                DownloadUtility.getDownloadUtility(this).download(uri, what);

                return true;
            case R.id.search_play_context_menu:

                data = (VKAudioItem)searchList.getAdapter().getItem(info.position);

                Intent intent = new Intent();
                intent.setAction(PlayMusicService.ACTION_PLAY);

                intent.putExtra(VKAudioItem.TITLE, data.title);
                intent.putExtra(VKAudioItem.ARTIST, data.artist);
                intent.putExtra(VKAudioItem.URL, data.url);

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
            SearchAudioTask searchAudioTask = new SearchAudioTask();
            searchAudioTask.setOnSearchAudioResponseListener(this);
    		searchAudioTask.execute(new VKAudioSearchRequest(q));
    	} 
    	((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
    		.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return restoreData;
    }
    
    public void onItemClick(AdapterView<?> list, View parent, int pos, long id) {
        VKAudioItem data = (VKAudioItem)searchList.getAdapter().getItem(pos);

        Intent intent = new Intent();
        intent.setAction(PlayMusicService.ACTION_PLAY);

        intent.putExtra(VKAudioItem.TITLE, data.title);
        intent.putExtra(VKAudioItem.ARTIST, data.artist);
        intent.putExtra(VKAudioItem.URL, data.url);

        startService(intent);
        startActivity(new Intent(this, MusicControlActivity.class));
    }

	@Override
	public void onSearchAudioResponse(VKAudioSearchResponse response) {
		restoreData = response;
		SearchAdapter adapter = new SearchAdapter(this, response);
		searchList.setAdapter(adapter);
	}
}
