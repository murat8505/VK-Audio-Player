package com.iradetskiy.vkapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;


public class VKApi {
	
	public static final String TAG = VKApi.class.getName();
	
	private String mUserId;
	private String mAccessToken;
	private static VKApi mApi;
	
	private final static String SCHEME = "https";
	public final static String AUTHORITY = "api.vk.com";
	public final static String PATH = "/method";
	public final static String ACCESS_TOKEN = "access_token";
	public final static String USER_ID = "user_id";
	
	private VKApi(String accessToken, String userId) {
		mAccessToken = accessToken;
		mUserId = userId;
	}

	public static VKApi getApi() {
		return mApi;
	}
	
	public static VKApi getApi(String accessToken, String userId) {
		if (mApi == null) {
			mApi = new VKApi(accessToken, userId);
		}
		return mApi;
	}
	
	public String getUserId() {
		return mUserId;
	}
	
	public String getAccessToken() {
		return mAccessToken;
	}
	
	public VKAudioSearchResponse searchAudio(VKAudioSearchRequest request) throws IOException, XmlPullParserException {
		
		VKAudioSearchResponse response = new VKAudioSearchResponse(getResponse(composeRequest("/audio.search.xml",
				new String[]{VKAudioSearchRequest.Q, VKAudioSearchRequest.AUTO_COMPLETE, VKAudioSearchRequest.SORT, 
					VKAudioSearchRequest.LYRICS, VKAudioSearchRequest.COUNT, VKAudioSearchRequest.OFFSET, ACCESS_TOKEN},
				new String[]{request.q, Integer.toString(request.auto_complete), Integer.toString(request.sort),
					Integer.toString(request.lyrics), Integer.toString(request.count), Integer.toString(request.offset), mAccessToken})));	
		
		response.setRequest(request);
		
        return response;
    }

	public VKUsersGetResponse getUsers(VKGetUsersRequest request) throws XmlPullParserException, IOException {
		
		HashMap<String, String> query = new HashMap<String, String>();
		query.put(VKGetUsersRequest.UIDS, request.uids);
		query.put(VKGetUsersRequest.FIELDS, request.fields);
		query.put(VKGetUsersRequest.NAME_CASE, request.name_case);
		query.put(ACCESS_TOKEN, mAccessToken);
		
		URI requestURI = composeRequest("/users.get.xml", query);
		String responseXML = getResponse(requestURI);
		VKUsersGetResponse response = new VKUsersGetResponse(responseXML);
		
		return response;
	}
	
	public VKAudioGetResponse getAudio(VKAudioGetRequest request) throws XmlPullParserException, IOException {
		
		VKAudioGetResponse response = new VKAudioGetResponse(getResponse(composeRequest("/audio.get.xml", 
			new String[]{VKAudioGetRequest.UID, VKAudioGetRequest.GID, VKAudioGetRequest.ALBUM_ID, VKAudioGetRequest.AIDS, VKAudioGetRequest.NEED_USER,
				VKAudioGetRequest.COUNT, VKAudioGetRequest.OFFSET, ACCESS_TOKEN}, 
			new String[]{request.uid, request.gid, request.album_id, request.aids, Integer.toString(request.need_user), 
				Integer.toString(request.count), Integer.toString(request.offset), mAccessToken})));
		
		response.setRequest(request);
		
		return response;
	}

    public boolean deleteAudio(VKAudioItem item) {
    	
        HashMap<String, String> query = new HashMap<String, String>();
        query.put(VKAudioItem.AID, item.aid);
        query.put(VKAudioItem.OID, item.owner_id);
        query.put(ACCESS_TOKEN, mAccessToken);
        
        getResponse(composeRequest("/audio.delete.xml", query));

        return true;
    }

    public void addAudio(VKAudioItem item){
    	
    	HashMap<String, String> query = new HashMap<String, String>();
        query.put(VKAudioItem.AID, item.aid);
        query.put(VKAudioItem.OID, item.owner_id);
        query.put(ACCESS_TOKEN, mAccessToken);

        getResponse(composeRequest("/audio.add.xml", query));
    }
	
    private URI composeRequest(String method, Map<String, String> query) {
    	String request = "";
		
		for (Map.Entry<String, String> item : query.entrySet()) {
			request += item.getKey() + "=" + item.getValue() + "&"; 
		}
		
		URI uri = null;
		
		try {
			uri = new URI(SCHEME, AUTHORITY, PATH + method,  request, null);
		} catch (URISyntaxException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return uri;
    }
    
	private URI composeRequest(String method, String[] keys, String[] values) {
		String request = "";
		
		for (int i = 0; i < keys.length; i++) {
			request += keys[i] + "=" + values[i] + "&";
		}
		
		URI uri = null;
		
		try {
			uri = new URI(SCHEME, AUTHORITY, PATH + method,  request, null);
		} catch (URISyntaxException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return uri;
	}

	private String getResponse(URI uri) {
		String response = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri); 
		
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

