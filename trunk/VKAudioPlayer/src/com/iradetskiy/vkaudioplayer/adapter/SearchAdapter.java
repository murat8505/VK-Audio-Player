package com.iradetskiy.vkaudioplayer.adapter;

import com.iradetskiy.vkapi.VKAudioSearchRequest;
import com.iradetskiy.vkapi.VKAudioSearchResponse;
import com.iradetskiy.vkaudioplayer.task.SearchAudioTask;

import android.app.Activity;

public class SearchAdapter extends AudioAdapter implements SearchAudioTask.OnSearchAudioResponseListener{

	private VKAudioSearchRequest mRequest;
	
	public SearchAdapter(Activity activity, VKAudioSearchResponse response) {
		mActivity = activity;
		mAudioList = response.getItems();
		mRequest = response.getRequest();
	}

	@Override
	protected void loadNext() {
		mRequest.offset = mAudioList.size() - 1;
		
		SearchAudioTask searchTask = new SearchAudioTask();
		searchTask.setOnSearchAudioResponseListener(this);
		searchTask.execute(mRequest);
	}

	@Override
	public void onSearchAudioResponse(VKAudioSearchResponse response) {
		mRequest = response.getRequest();
		mAudioList.addAll(response.getItems());
		notifyDataSetChanged();
	}
}
