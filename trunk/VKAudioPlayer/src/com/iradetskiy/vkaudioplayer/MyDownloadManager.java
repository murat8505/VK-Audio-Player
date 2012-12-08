package com.iradetskiy.vkaudioplayer;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class MyDownloadManager {
    private DownloadManager downloadManager;
    private Context context;
    public MyDownloadManager(Context context) {
        this.context = context;
        downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    public void download(String uri, String what) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        request.setTitle(what + ".mp3");
        long enqueue = downloadManager.enqueue(request);
        Toast.makeText(context, "Download " + what, Toast.LENGTH_SHORT).show();
    }
}
