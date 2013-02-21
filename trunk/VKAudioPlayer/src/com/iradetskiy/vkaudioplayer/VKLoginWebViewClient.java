package com.iradetskiy.vkaudioplayer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.iradetskiy.vkapi.VKApi;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VKLoginWebViewClient extends WebViewClient {
	
	OnVKLoginListener loginListener;
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String newUrl){
		try {
			URL url = new URL(newUrl);
			String params = url.getRef();
			if (params == null){
				return false;
			}
			Map<String, String> map = parseParams(url.getRef());
			String accessToken = map.get(VKApi.ACCESS_TOKEN);
			String userId = map.get(VKApi.USER_ID);
			if (accessToken != null && loginListener != null) {
				loginListener.onLogin(accessToken, userId);
				return true;
			}
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	private Map<String, String> parseParams(String params){
		Map<String, String> map = new HashMap<String, String>();
		String[] keyVaules = params.split("&");
		
		for(int i = 0; i < keyVaules.length; i++){
			String[] pair = keyVaules[i].split("=");
			map.put(pair[0], pair[1]);
		}
		
		return map;
	}
	
	public void setOnVKLoginListener(OnVKLoginListener listener){
		loginListener = listener;
	}
}
