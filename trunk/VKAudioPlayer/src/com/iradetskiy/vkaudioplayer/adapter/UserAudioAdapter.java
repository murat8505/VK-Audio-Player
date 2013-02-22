package com.iradetskiy.vkaudioplayer.adapter;

import android.app.Activity;

import com.iradetskiy.vkapi.VKAudioGetRequest;
import com.iradetskiy.vkapi.VKAudioGetResponse;
import com.iradetskiy.vkaudioplayer.task.GetAudioTask;


public class UserAudioAdapter extends AudioAdapter implements GetAudioTask.OnGetAudioResponseListener{

	private VKAudioGetRequest mRequest;
	
	public UserAudioAdapter(Activity activity, VKAudioGetResponse response) {
		mActivity = activity;
		mAudioList = response.getItems();
		mRequest = response.getRequest();
	}
	
	@Override
	protected void loadNext() {
		mRequest.offset = mAudioList.size() - 1;
		
		GetAudioTask getAudioTask = new GetAudioTask();
		getAudioTask.setOnGetAudioResponseListener(this);
		getAudioTask.execute(mRequest);
	}

	@Override
	public void onGetAudioResponse(VKAudioGetResponse response) {
		mRequest = response.getRequest();
		mAudioList.addAll(response.getItems());
		notifyDataSetChanged();
	}
}
