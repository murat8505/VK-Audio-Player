package com.example.a2dptest;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class VKAudio {
	private String mAccessToken;
	public final static String TAG = "VKAudio"; 
	
	public final static String API_HOST = "https://api.vk.com/method/";
	public final static String AUDIO_SEARCH = "audio.search.xml";
	public final static String Q = "q";
	public final static String AUTO_COMPLETE = "auto_complete";
	public final static String SORT = "sort";
	public final static String LYRICS = "lyrics";
	public final static String COUNT = "count";
	public final static String OFFSET = "offset";
	public final static String ACCESS_TOKEN = "access_token";
	
	public VKAudio(String accessToken){
		mAccessToken = accessToken;
	}
	
	public VKAudioSearchResponse search(String q, String auto_complete, String sort, String lyrics, Integer count, Integer offset) throws XmlPullParserException, IOException{
		
		String response = "";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(API_HOST + 
				AUDIO_SEARCH + "?" +
				Q + "=" + q + "&" +
				AUTO_COMPLETE + "=" + auto_complete + "&" +
				SORT + "=" + sort + "&" +
				LYRICS + "=" + lyrics + "&" +
				COUNT + "=" + count.toString() + "&" +
				OFFSET + "=" + offset.toString() + "&" +
				ACCESS_TOKEN + "=" + mAccessToken); 
		
		try {
			response = EntityUtils.toString(client.execute(request).getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d(TAG, "some shit");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "another shit");
		}
		return new VKAudioSearchResponse(response);
	}
}
