package com.app.mp3downloader.downloadHandler;

import static com.app.mp3downloader.downloadHandler.DownloaderWorker.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopDownloadBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        downloader.put("stop",true);
        System.out.println("workInfo StopDownloadBroadcast");
    }
}
