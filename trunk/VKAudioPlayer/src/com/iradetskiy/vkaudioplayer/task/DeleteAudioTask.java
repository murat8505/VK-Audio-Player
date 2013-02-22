package com.iradetskiy.vkaudioplayer.task;

import android.os.AsyncTask;

import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKAudioItem;

public class DeleteAudioTask extends AsyncTask<VKAudioItem, Void, Integer>{
	public static final String TAG = DeleteAudioTask.class.getName();

	private OnAudioDeletedListener mListener;
	
	public void setOnAudioDeletedListener(OnAudioDeletedListener listener) {
		mListener = listener;
	}
	
	@Override
	protected Integer doInBackground(VKAudioItem... params) {
		VKApi.getApi().deleteAudio(params[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer param) {
		if (mListener != null) {
			mListener.onAudioDeleted(param);
		}
	}
	
	public interface OnAudioDeletedListener {
		void onAudioDeleted(Integer code);
	}
}
