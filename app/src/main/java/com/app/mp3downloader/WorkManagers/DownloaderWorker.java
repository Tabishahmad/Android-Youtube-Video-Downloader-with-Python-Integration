//package com.app.mp3downloader.downloadHandler;
//
//import static android.content.Context.NOTIFICATION_SERVICE;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.TaskStackBuilder;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Environment;
//import android.util.Log;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.app.mp3downloader.R;
//import com.app.mp3downloader.presentation.FragmentContainerActivity;
//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
//
//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.annotations.NonNull;
//
//public class DownloaderWorker extends Worker {
//
//    public static final String FORMAT = "FORMAT";
//    public static final String DIRECTORY = "DIRECTORY";
//    public static final String MAX_QUALITY = "MAX_QUALITY";
//    public static final String VIDEO_URL = "VIDEO_URL";
//    public static PyObject downloader;
//    NotificationManagerCompat mNotificationManager;
//    NotificationCompat.Builder notifBuilder;
//    PendingIntent activityPendingIntent;
//
//    public DownloaderWorker(@NonNull Context context, @NonNull WorkerParameters params){
//        super(context, params);
//    }
//    @Override
//    public Result doWork(){
//        String format = getInputData().getString(FORMAT);
//        String directory = getInputData().getString(DIRECTORY);
//        String maxQuality = getInputData().getString(MAX_QUALITY);
//        String videoUrl = getInputData().getString(VIDEO_URL);
//        File downloadDirectory = new File(directory);
//        boolean success;
//
//        makeNotificationChannel("CHANNEL", "Download status", NotificationManager.IMPORTANCE_DEFAULT);
//        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
//        notifBuilder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL");
//
//
//        Intent activityIntent = new Intent(getApplicationContext(), FragmentContainerActivity.class);
//        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        activityIntent.setClass(getApplicationContext(), FragmentContainerActivity.class);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//        stackBuilder.addNextIntentWithParentStack(activityIntent);
//        activityPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,activityIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        if(!downloadDirectory.exists()){
//            success = downloadDirectory.mkdirs();
//            if(!success){
//                notifyFail(mNotificationManager, notifBuilder, activityPendingIntent);
//                return Result.failure();
//            }
//        }
//
//        Python py = Python.getInstance();
//        PyObject pyf;
//
//        notifyDownloading(mNotificationManager, notifBuilder, getApplicationContext().getResources().getString(R.string.text_notif_downloading), activityPendingIntent);
//        String module = "yt-dlp";
//        pyf = py.getModule("downloader");
//        downloader = pyf.callAttr("downloader", videoUrl, format, directory, maxQuality, Environment.getExternalStorageDirectory().getPath()+"/Android/data/com.acmo0.youtubedownloader");
//        PyObject download = downloader.callAttr("download");
//        System.out.println("Started downloading");
//        boolean process_fail = false;
//        while (downloader.callAttr("state").toBoolean()!=false || process_fail){
//            notifBuilder.setContentText(downloader.get("status").toString());
//            mNotificationManager.notify(1,notifBuilder.build());
//            process_fail = downloader.callAttr("isFail").toBoolean();
//            System.out.println("State : "+Boolean.toString(process_fail));
//            if(process_fail){
//                System.out.println("ERROR");
//                mNotificationManager.cancelAll();
//                notifyFail(mNotificationManager, notifBuilder, activityPendingIntent);
//                return Result.failure();
//            }
//            try {
//                TimeUnit.MILLISECONDS.sleep(600);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//        System.out.println("State : "+Boolean.toString(process_fail));
//        process_fail = downloader.callAttr("isFail").toBoolean();
//        if(process_fail == false){
//            notifySuccess(mNotificationManager, notifBuilder, activityPendingIntent);
//            return Result.success();
//        }
//        if ((videoUrl.contains("youtu.be") || videoUrl.contains("youtube.com")) && process_fail && Build.VERSION.SDK_INT < 29) {
//            pyf = py.getModule("ytb_downloader");
//            module = "pytube";
//            downloader = pyf.callAttr("downloader", videoUrl, format, directory, maxQuality, Environment.getExternalStorageDirectory().getPath()+"/Android/data/com.acmo0.youtubedownloader");
//            download = downloader.callAttr("download");
//            System.out.println("Started downloading");
//            while (downloader.callAttr("state").toBoolean()!=false){
//                notifBuilder.setContentText(downloader.get("status").toString());
//                mNotificationManager.notify(1,notifBuilder.build());
//                process_fail = downloader.callAttr("isFail").toBoolean();
//                if(process_fail){
//                    mNotificationManager.cancelAll();
//                    notifyFail(mNotificationManager, notifBuilder, activityPendingIntent);
//                    return Result.failure();
//                }
//                try {
//                    TimeUnit.MILLISECONDS.sleep(600);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        process_fail = downloader.callAttr("isFail").toBoolean();
//
//        if(process_fail == false && module.equals("pytube")){
//            FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
//            loadFFmpeg(ffmpeg);
//            if (format.equals("m4a")) {
//                System.out.println("Converting");
//                ffmpegExecute(ffmpeg,downloader.callAttr("cmd").toJava(String[].class));
//            }
//            notifySuccess(mNotificationManager, notifBuilder, activityPendingIntent);
//            return Result.success();
//        }
//        mNotificationManager.cancelAll();
//        notifyFail(mNotificationManager, notifBuilder, activityPendingIntent);
//        return Result.failure();
//    }
//
//    @Override
//    public void onStopped(){
//        System.out.println("STOOOOOOP");
//        downloader.put("stop",true);
//        try {
//            TimeUnit.MILLISECONDS.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mNotificationManager.cancelAll();
//        notifyStop(mNotificationManager, notifBuilder, activityPendingIntent);
//        super.onStopped();
//    }
//    void notifyDownloading(NotificationManagerCompat mNotificationManager, NotificationCompat.Builder notifBuilder, String text, PendingIntent mPendingIntent){
//        Intent stopIntent = new Intent(getApplicationContext(), StopDownloadBroadcast.class);
//        PendingIntent stopPIntent = PendingIntent.getBroadcast(getApplicationContext(),1,stopIntent,PendingIntent.FLAG_IMMUTABLE);
//        notifBuilder
//                .setContentIntent(mPendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Video Downloader")
//                .setContentText(text)
//                .setProgress(0, 0, true)
//                .setOnlyAlertOnce(true)
//                .addAction(R.drawable.ic_background,"Cancel",stopPIntent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        mNotificationManager.notify(1, notifBuilder.build());
//    }
//    void notifyFail(NotificationManagerCompat mNotificationManager, NotificationCompat.Builder notifBuilder, PendingIntent mPendingIntent){
//        notifBuilder.setContentIntent(mPendingIntent).setSmallIcon(R.mipmap.ic_launcher).setContentText(getApplicationContext().getResources().getString(R.string.text_notif_download_error)).setProgress(0,0,false);
//        mNotificationManager.notify(1, notifBuilder.build());
//    }
//    void notifySuccess(NotificationManagerCompat mNotificationManager, NotificationCompat.Builder notifBuilder, PendingIntent mPendingIntent){
//        notifBuilder.setContentIntent(mPendingIntent).setSmallIcon(R.mipmap.ic_launcher).setContentText(getApplicationContext().getResources().getString(R.string.text_notif_download_finished)).setProgress(0,0,false);
//        mNotificationManager.notify(1, notifBuilder.build());
//    }
//    void notifyStop(NotificationManagerCompat mNotificationManager, NotificationCompat.Builder notifBuilder, PendingIntent mPendingIntent){
//        notifBuilder.setContentIntent(mPendingIntent).setSmallIcon(R.mipmap.ic_launcher).setContentText(getApplicationContext().getResources().getString(R.string.text_notif_stop)).setProgress(0,0,false);
//        mNotificationManager.notify(1, notifBuilder.build());
//    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    void makeNotificationChannel(String id, String name, int importance)
//    {
//        NotificationChannel channel = new NotificationChannel(id, name, importance);
//
//        NotificationManager notificationManager =
//                (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
//
//        assert notificationManager != null;
//        notificationManager.createNotificationChannel(channel);
//    }
//    void loadFFmpeg(FFmpeg ffmpeg){
//        try{
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler(){
//                @Override
//                public void onStart(){}
//                @Override
//                public void onFailure(){}
//                @Override
//                public void onSuccess() {}
//
//                @Override
//                public void onFinish() {}
//            });
//        }catch (FFmpegNotSupportedException e){
//            Log.e("ERROR",e.toString());
//        }
//    }
//    void ffmpegExecute(FFmpeg ffmpeg,String[] cmd){
//        try{
//            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler(){
//                @Override
//                public void onStart() {}
//
//                @Override
//                public void onProgress(String message) {System.out.println(message);}
//
//                @Override
//                public void onFailure(String message) {}
//
//                @Override
//                public void onSuccess(String message) {}
//
//                @Override
//                public void onFinish() {
//                    for(int i = 0;i<cmd.length;i++){
//                        if (cmd[i].equals("-i")) {
//                            String filename = cmd[i+1];
//                            System.out.println(filename);
//                            File tempfile = new File(filename);
//                            tempfile.delete();
//                        }
//                    }
//
//                }
//            });
//        }catch (FFmpegCommandAlreadyRunningException e){
//            Log.d("ERROR", e.toString());
//        }
//    }
//}
