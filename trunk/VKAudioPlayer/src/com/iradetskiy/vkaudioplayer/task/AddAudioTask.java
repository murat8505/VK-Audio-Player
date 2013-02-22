package com.iradetskiy.vkaudioplayer.task;

import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioItem;

import android.os.AsyncTask;

public class AddAudioTask extends AsyncTask<VKAudioItem, Void, Integer> {
	public static final String TAG = DeleteAudioTask.class.getName();

	private OnAudioAddedListener mListener;
	
	public void setOnAudioAddedListener(OnAudioAddedListener listener) {
		mListener = listener;
	}
	
	@Override
	protected Integer doInBackground(VKAudioItem... params) {
		VKApi.getApi().addAudio(params[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer param) {
		if (mListener != null) {
			mListener.onAudioAdded(param);
		}
	}
	
	public interface OnAudioAddedListener {
		void onAudioAdded(Integer code);
	}
}
