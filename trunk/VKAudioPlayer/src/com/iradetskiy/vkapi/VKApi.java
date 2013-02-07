package com.iradetskiy.vkapi;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;


public class VKApi {
	
	private String mAccessToken;
	private static VKApi api;
	
	public final static String API_HOST = "https://api.vk.com/method/";
	public final static String ACCESS_TOKEN = "access_token";
	public final static String USER_ID = "user_id";
	
	private VKApi(String accessToken){
		mAccessToken = accessToken;
	}
	
	public static VKApi getApi(String accessToken) {
		if (api == null) {
			if (accessToken != null) {
				api = new VKApi(accessToken);
			} else {
				api = null;
			}
		}
		return api;
	}
	
	public VKAudioSearchResponse searchAudio(String q, String auto_complete, String sort, String lyrics, Integer count, Integer offset) throws IOException, XmlPullParserException {

        String[] keys = {"q", "auto_complete", "sort", "lyrics", "count", "offset", ACCESS_TOKEN};
        String[] values = {q, auto_complete, sort, lyrics, count.toString(), offset.toString(), mAccessToken};

        return new VKAudioSearchResponse(getResponse(composeRequest("audio.search.xml", keys, values)));
    }

	public VKUsersGetResponse getUsers(String uids, String fields, String name_case) throws XmlPullParserException, IOException {
		
		String[] keys = {"uids", "fields", "name_case", ACCESS_TOKEN};
		String[] values = {uids, fields, name_case, mAccessToken};
		
		return new VKUsersGetResponse(getResponse(composeRequest("users.get.xml", keys, values)));
	}
	
	public VKAudioGetResponse getAudio(String uid, String gid, String album_id, String aids, String need_user, String count, String offset) throws XmlPullParserException, IOException {
		
		String[] keys = {"uid", "gid", "album_id", "aids", "need_user", "count", "offset", ACCESS_TOKEN};
		String[] values = {uid, gid, album_id, aids, need_user, count, offset, mAccessToken};
		
		return new VKAudioGetResponse(getResponse(composeRequest("audio.get.xml", keys, values)));
	}

    public boolean deleteAudio(String aid, String oid) {
        String[] keys = {"aid", "oid", ACCESS_TOKEN};
        String[] values = {aid, oid, mAccessToken};

        getResponse(composeRequest("audio.delete.xml", keys, values));

        return true;
    }

    public void addAudio(String aid, String oid){
        String[] keys = {"aid", "oid", ACCESS_TOKEN};
        String[] values = {aid, oid, mAccessToken};

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

