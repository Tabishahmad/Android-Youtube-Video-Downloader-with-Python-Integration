//package com.app.mp3downloader.WorkManagers
//
//import android.content.Context
//import android.os.Build
//import android.os.Environment
//import android.util.Log
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.app.mp3downloader.common.DIRECTORY
//import com.app.mp3downloader.common.FORMAT
//import com.app.mp3downloader.common.MAX_QUALITY
//import com.app.mp3downloader.common.VIDEO_URL
//import com.chaquo.python.PyObject
//import com.chaquo.python.Python
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
//import java.io.File
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//
//class DownloaderWorker @Inject constructor(private val context: Context, workerParams: WorkerParameters) :
//    Worker(context, workerParams) {
//
//    companion object {
////        const val FORMAT = "FORMAT"
////        const val DIRECTORY = "DIRECTORY"
////        const val MAX_QUALITY = "MAX_QUALITY"
////        const val VIDEO_URL = "VIDEO_URL"
//
//    }
//    lateinit var downloader: PyObject
////    private lateinit var mNotificationManager: NotificationManagerCompat
////    private lateinit var notifBuilder: NotificationCompat.Builder
////    private lateinit var activityPendingIntent: PendingIntent
//
//    override  fun doWork(): Result {
//        val format = inputData.getString(FORMAT)
//        val directory = inputData.getString(DIRECTORY)
//        val maxQuality = inputData.getString(MAX_QUALITY)
//        val videoUrl = inputData.getString(VIDEO_URL)
//        val downloadDirectory = File(directory)
//        var success: Boolean
//
////        makeNotificationChannel("CHANNEL", "Download status", NotificationManager.IMPORTANCE_DEFAULT)
////        mNotificationManager = NotificationManagerCompat.from(context)
////        notifBuilder = NotificationCompat.Builder(context, "CHANNEL")
//
////        val activityIntent = Intent(context, FragmentContainerActivity::class.java)
////        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER)
////        activityIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
////        val stackBuilder = androidx.core.app.TaskStackBuilder.create(context)
////        stackBuilder.addNextIntentWithParentStack(activityIntent)
////        activityPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)!!
//
//        if (!downloadDirectory.exists()) {
//            success = downloadDirectory.mkdirs()
//            if (!success) {
//                notifyFail()
//                return Result.failure()
//            }
//        }
//
//        val py = Python.getInstance()
//        var pyf: PyObject
//
////        notifyDownloading(
////            mNotificationManager,
////            notifBuilder,
////            context.getString(R.string.text_notif_downloading),
////            activityPendingIntent
////        )
//        var module = "yt-dlp"
//        pyf = py.getModule("downloader")
//        downloader = pyf.callAttr("downloader", videoUrl, format, directory, maxQuality, Environment.getExternalStorageDirectory().path + "/Android/data/com.app.mp3downloader")
//        var download = downloader.callAttr("download")
//        println("Started downloading")
//        var processFail = false
//        while (downloader.callAttr("state").toBoolean() != false || processFail) {
////            notifBuilder.setContentText(downloader.get("status").toString())
////            mNotificationManager.notify(1, notifBuilder.build())
//            processFail = downloader.callAttr("isFail").toBoolean()
//            println("State : $processFail")
//            if (processFail) {
//                println("ERROR")
////                mNotificationManager.cancelAll()
//                notifyFail()
//                return Result.failure()
//            }
//            try {
//                TimeUnit.MILLISECONDS.sleep(600)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//        println("State : $processFail")
//        processFail = downloader.callAttr("isFail").toBoolean()
//        if (!processFail) {
//            notifySuccess()
//            return Result.success()
//        }
//        if ((videoUrl?.contains("youtu.be") == true || videoUrl!!.contains("youtube.com")) && processFail && Build.VERSION.SDK_INT < 29) {
//            pyf = py.getModule("ytb_downloader")
//            module = "pytube"
//            downloader = pyf.callAttr("downloader", videoUrl, format, directory, maxQuality, Environment.getExternalStorageDirectory().path + "/Android/data/com.acmo0.youtubedownloader")
//            download = downloader.callAttr("download")
//            println("Started downloading")
//            while (downloader.callAttr("state").toBoolean() != false) {
////                notifBuilder.setContentText(downloader.get("status").toString())
////                mNotificationManager.notify(1, notifBuilder.build())
//                processFail = downloader.callAttr("isFail").toBoolean()
//                if (processFail) {
////                    mNotificationManager.cancelAll()
//                    notifyFail()
//                    return Result.failure()
//                }
//                try {
//                    TimeUnit.MILLISECONDS.sleep(600)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        processFail = downloader.callAttr("isFail").toBoolean()
//
//        if (!processFail && module == "pytube") {
//            val ffmpeg = FFmpeg.getInstance(context)
//            loadFFmpeg(ffmpeg)
//            if (format == "m4a") {
//                println("Converting")
//                ffmpegExecute(
//                    ffmpeg, downloader.callAttr("cmd").toJava<Array<String>>(
//                        Array<String>::class.java
//                    )
//                )
//            }
//            notifySuccess()
//            return Result.success()
//        }
////        mNotificationManager.cancelAll()
//        notifyFail()
//        return Result.failure()
//    }
//
//    override fun onStopped() {
//        println("STOOOOOOP")
//        downloader.put("stop", true)
//        try {
//            TimeUnit.MILLISECONDS.sleep(3000)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
////        mNotificationManager.cancelAll()
//        notifyStop()
//        super.onStopped()
//    }
//
////    private fun notifyDownloading(
////        mNotificationManager: NotificationManagerCompat,
////        notifBuilder: NotificationCompat.Builder,
////        text: String,
////        mPendingIntent: PendingIntent
////    ) {
////        val stopIntent = Intent(context, StopDownloadBroadcast::class.java)
////        val stopPIntent = PendingIntent.getBroadcast(context, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE)
////        notifBuilder.setContentIntent(mPendingIntent)
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .setContentTitle("Video Downloader")
////            .setContentText(text)
////            .setProgress(0, 0, true)
////            .setOnlyAlertOnce(true)
////            .addAction(R.drawable.ic_background, "Cancel", stopPIntent)
////            .priority = NotificationCompat.PRIORITY_DEFAULT
////        mNotificationManager.notify(1, notifBuilder.build())
////    }
//    private fun notifyFail(){
//
//    }
////    private fun notifyFail(
////        mNotificationManager: NotificationManagerCompat,
////        notifBuilder: NotificationCompat.Builder,
////        mPendingIntent: PendingIntent
////    ) {
////        notifBuilder.setContentIntent(mPendingIntent)
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .setContentText(context.resources.getString(R.string.text_notif_download_error))
////            .setProgress(0, 0, false)
////        mNotificationManager.notify(1, notifBuilder.build())
////    }
//private fun notifySuccess(
//
//) {
//
//}
////    private fun notifySuccess(
////        mNotificationManager: NotificationManagerCompat,
////        notifBuilder: NotificationCompat.Builder,
////        mPendingIntent: PendingIntent
////    ) {
////        notifBuilder.setContentIntent(mPendingIntent)
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .setContentText(context.resources.getString(R.string.text_notif_download_finished))
////            .setProgress(0, 0, false)
////        mNotificationManager.notify(1, notifBuilder.build())
////    }
//private fun notifyStop(){
//
//}
////    private fun notifyStop(
////        mNotificationManager: NotificationManagerCompat,
////        notifBuilder: NotificationCompat.Builder,
////        mPendingIntent: PendingIntent
////    ) {
////        notifBuilder.setContentIntent(mPendingIntent)
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .setContentText(context.resources.getString(R.string.text_notif_stop))
////            .setProgress(0, 0, false)
////        mNotificationManager.notify(1, notifBuilder.build())
////    }
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun makeNotificationChannel(id: String, name: String, importance: Int) {
////        val channel = NotificationChannel(id, name, importance)
////        val notificationManager =
////            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        notificationManager.createNotificationChannel(channel)
////    }
//
//    private fun loadFFmpeg(ffmpeg: FFmpeg) {
//        try {
//            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
//                override fun onStart() {}
//                override fun onFailure() {}
//                override fun onSuccess() {}
//                override fun onFinish() {}
//            })
//        } catch (e: FFmpegNotSupportedException) {
//            Log.e("ERROR", e.toString())
//        }
//    }
//
//    private fun ffmpegExecute(ffmpeg: FFmpeg, cmd: Array<String>) {
//        try {
//            ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
//                override fun onStart() {}
//                override fun onProgress(message: String) {
//                    println(message)
//                }
//
//                override fun onFailure(message: String) {}
//                override fun onSuccess(message: String) {}
//                override fun onFinish() {
//                    for (i in cmd.indices) {
//                        if (cmd[i] == "-i") {
//                            val filename = cmd[i + 1]
//                            println(filename)
//                            val tempfile = File(filename)
//                            tempfile.delete()
//                        }
//                    }
//                }
//            })
//        } catch (e: FFmpegCommandAlreadyRunningException) {
//            Log.d("ERROR", e.toString())
//        }
//    }
//}
