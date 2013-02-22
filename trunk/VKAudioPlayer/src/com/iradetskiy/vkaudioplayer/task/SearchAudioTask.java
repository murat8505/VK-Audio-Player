package com.iradetskiy.vkaudioplayer.task;

import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioSearchRequest;
import com.iradetskiy.vkapi.VKAudioSearchResponse;

import android.os.AsyncTask;
import android.util.Log;

public class SearchAudioTask extends AsyncTask<VKAudioSearchRequest, Void, VKAudioSearchResponse>{
	
	public static final String TAG = SearchAudioTask.class.getName();

	private OnSearchAudioResponseListener mListener;
	
	public void setOnSearchAudioResponseListener(OnSearchAudioResponseListener listener) {
		mListener = listener;
	}
	
	@Override
	protected VKAudioSearchResponse doInBackground(VKAudioSearchRequest... params) {
		VKAudioSearchResponse response = null;
		
		try {
			response = VKApi.getApi().searchAudio(params[0]);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return response;
	}

	@Override
	protected void onPostExecute(VKAudioSearchResponse response) {
		mListener.onSearchAudioResponse(response);
	}
	
	public interface OnSearchAudioResponseListener {
		void onSearchAudioResponse(VKAudioSearchResponse response);
	}
}
