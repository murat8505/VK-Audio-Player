package com.iradetskiy.vkaudioplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CurrentUserAudioActivity extends Activity {

	VKApi mApi;
	public static final String[] from = {"song", "artist", "duration", "url"}; 
	ListView searchList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String userId = (String) extras.get(VKApi.USER_ID);
		String accessToken = (String) extras.get(VKApi.ACCESS_TOKEN);

		searchList = (ListView)findViewById(R.id.searchResultsList);
		
		mApi = new VKApi(accessToken);
		new LoadUserNameTask().execute(userId);
		new LoadAudioGetResultsTask().execute(userId);
	}

	private class LoadAudioGetResultsTask extends
			AsyncTask<String, Void, VKAudioGetResponse> {

		@Override
		protected VKAudioGetResponse doInBackground(String... arg0) {
			VKAudioGetResponse response = null;

			try {
				response = mApi.getAudio("", "", "", "", "", "", "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(VKAudioGetResponse response) {

			int[] to = { R.id.song, R.id.artist, R.id.duration };

			ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
					response.getItems().size());

			for (int i = 0; i < response.getItems().size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(from[0], response.getItems().get(i).title);
				map.put(from[1], response.getItems().get(i).artist);
				map.put(from[2], response.getItems().get(i).duration);
				map.put(from[3], response.getItems().get(i).url);

				data.add(map);
			}

			SimpleAdapter adapter = new SimpleAdapter(CurrentUserAudioActivity.this,
					data, R.layout.audio_item, from, to);
			searchList.setAdapter(adapter);
		}
	}

	private class LoadUserNameTask extends
			AsyncTask<String, Void, VKUsersGetResponse> {

		@Override
		protected VKUsersGetResponse doInBackground(String... arg0) {
			VKUsersGetResponse response = null;

			try {
				response = mApi
						.getUsers(arg0[0], "first_name,last_name", "nom");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(VKUsersGetResponse response) {
			CurrentUserAudioActivity.this
					.setTitle(response.getResults().get(0).first_name + " "
							+ response.getResults().get(0).last_name);
		}
	}
}