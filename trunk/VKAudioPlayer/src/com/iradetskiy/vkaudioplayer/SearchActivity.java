package com.iradetskiy.vkaudioplayer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class SearchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }
    
    public void onSearchButtonClick(View v){
    	
    }
}
