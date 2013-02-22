package com.iradetskiy.vkaudioplayer.task;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.iradetskiy.vkapi.VKApi;
import com.iradetskiy.vkapi.VKGetUsersRequest;
import com.iradetskiy.vkapi.VKUsersGetResponse;

import android.os.AsyncTask;

public class GetUsersTask extends AsyncTask<VKGetUsersRequest, Void, VKUsersGetResponse> {

	private OnGetUsersListener mListener;
	
	@Override
	protected VKUsersGetResponse doInBackground(VKGetUsersRequest... params) {
		
		VKUsersGetResponse response = null;
		
		try {
			response = VKApi.getApi().getUsers(params[0]);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	@Override
	protected void onPostExecute(VKUsersGetResponse response) {
		mListener.onGetUsersResponse(response);
	}
	
	public void setOnGetUsersListener(OnGetUsersListener listener) {
		mListener = listener;
	}
	
	public interface OnGetUsersListener {
		void onGetUsersResponse(VKUsersGetResponse response);
	}
}
