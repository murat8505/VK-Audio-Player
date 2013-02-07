package com.iradetskiy.utility;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class DownloadUtility {
	
	private static DownloadUtility downloadUtility; 
	private DownloadManager mDownloadManager;
	private Context mContext;
	private DownloadUtility(Context context) {
		mContext = context;
		mDownloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public static DownloadUtility getDownloadUtility(Context context) {
		if (downloadUtility == null) {
			downloadUtility = new DownloadUtility(context);
		}
		return downloadUtility;
	}
	
    public void download(String uri, String what) {
    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        request.setTitle(what + ".mp3");
        mDownloadManager.enqueue(request);
        Toast.makeText(mContext, "Download " + what, Toast.LENGTH_SHORT).show();
    }
}
