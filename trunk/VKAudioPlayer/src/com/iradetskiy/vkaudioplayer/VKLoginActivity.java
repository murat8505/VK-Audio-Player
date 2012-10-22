package com.iradetskiy.vkaudioplayer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;

public class VKLoginActivity extends Activity implements OnVKLoginListener {

	WebView loginPage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
        VKLoginWebViewClient webViewClient = new VKLoginWebViewClient();
        webViewClient.setOnVKLoginListener(this);
        
        loginPage = new WebView(this);
        loginPage.setWebViewClient(webViewClient);
        loginPage.loadUrl("http://oauth.vk.com/oauth/authorize?client_id=2795250&scope=audio&redirect_uri=http://oauth.vk.com/blank.html&display=wap&response_type=token");
        
        setContentView(loginPage);
    }

	public void onLogin(String userId, String accessToken) {
		
		Intent intent = new Intent(this, CurrentUserAudioActivity.class);
		intent.putExtra(VKApi.USER_ID, userId);
		intent.putExtra(VKApi.ACCESS_TOKEN, accessToken);
		
		startActivity(intent);
	}
}