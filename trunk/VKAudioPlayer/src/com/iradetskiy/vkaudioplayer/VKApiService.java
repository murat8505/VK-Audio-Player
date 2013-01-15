package com.iradetskiy.vkaudioplayer;

import java.io.IOException;

import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class VKApiService extends Service {

	final IBinder binder = new VKApiBinder();
	
	public class VKApiBinder extends Binder{
		
		public VKApiService getService(){
			
			return VKApiService.this;
		}
	}
	
	private static String accessToken;
	
	public final static String API_HOST = "https://api.vk.com/method/";
	public final static String ACCESS_TOKEN = "access_token";
	public final static String USER_ID = "user_id";
    private String uid;

    public String getUserId() {
        return uid;
    }
	
	@Override
	public IBinder onBind(Intent intent) {

        if (intent.hasExtra(ACCESS_TOKEN)){
            accessToken = (String)intent.getExtras().get(ACCESS_TOKEN);
            Log.d("VKApiService", "onBind: accessToken = " + accessToken);
        }

		return binder;
	}

    public VKAudioSearchResponse searchAudio(String q, String auto_complete, String sort, String lyrics, Integer count, Integer offset) throws IOException, XmlPullParserException {

        String[] keys = {"q", "auto_complete", "sort", "lyrics", "count", "offset", ACCESS_TOKEN};
        String[] values = {q, auto_complete, sort, lyrics, count.toString(), offset.toString(), accessToken};

        return new VKAudioSearchResponse(getResponse(composeRequest("audio.search.xml", keys, values)));
    }

	public VKUsersGetResponse getUsers(String uids, String fields, String name_case) throws XmlPullParserException, IOException {
		
		String[] keys = {"uids", "fields", "name_case", ACCESS_TOKEN};
		String[] values = {uids, fields, name_case, accessToken};
        uid = uids;
		
		return new VKUsersGetResponse(getResponse(composeRequest("users.get.xml", keys, values)));
	}
	
	public VKAudioGetResponse getAudio(String uid, String gid, String album_id, String aids, String need_user, String count, String offset) throws XmlPullParserException, IOException {
		
		String[] keys = {"uid", "gid", "album_id", "aids", "need_user", "count", "offset", ACCESS_TOKEN};
		String[] values = {uid, gid, album_id, aids, need_user, count, offset, accessToken};
		
		return new VKAudioGetResponse(getResponse(composeRequest("audio.get.xml", keys, values)));
	}

    public boolean deleteAudio(String aid, String oid) {
        String[] keys = {"aid", "oid", ACCESS_TOKEN};
        String[] values = {aid, oid, accessToken};

        getResponse(composeRequest("audio.delete.xml", keys, values));

        return true;
    }

    public void addAudio(String aid, String oid){
        String[] keys = {"aid", "oid", ACCESS_TOKEN};
        String[] values = {aid, oid, accessToken};

        getResponse(composeRequest("audio.add.xml", keys, values));
    }
	
	private String composeRequest(String method, String[] keys, String[] values){
		String request = API_HOST + method + "?";
		
		for (int i = 0; i < keys.length; i++) {
			request += keys[i] + "=" + values[i] + "&";
		}

        System.out.println(request);

		return request;
	}

	private String getResponse(String requestString) {
		String response = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(requestString); 
		
		try {
			response = EntityUtils.toString(client.execute(request).getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
