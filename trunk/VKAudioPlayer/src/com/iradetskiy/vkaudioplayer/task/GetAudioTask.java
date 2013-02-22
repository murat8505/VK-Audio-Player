package com.iradetskiy.vkaudioplayer.task;

import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioGetRequest;
import com.iradetskiy.vkapi.VKAudioGetResponse;

import android.os.AsyncTask;
import android.util.Log;

public class GetAudioTask extends AsyncTask<VKAudioGetRequest, Void, VKAudioGetResponse> {

	public static final String TAG = GetAudioTask.class.getName();

	private OnGetAudioResponseListener mListener;
	
	public void setOnGetAudioResponseListener(OnGetAudioResponseListener listener) {
		mListener = listener;
	}
	
	@Override
	protected VKAudioGetResponse doInBackground(VKAudioGetRequest... params) {

		VKAudioGetResponse response = null;
		
		try {
			response = VKApi.getApi().getAudio(params[0]);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return response;
	}
	
	@Override
	protected void onPostExecute(VKAudioGetResponse response) {
		mListener.onGetAudioResponse(response);
	}
	
	public interface OnGetAudioResponseListener {
		void onGetAudioResponse(VKAudioGetResponse response);
	}
}
