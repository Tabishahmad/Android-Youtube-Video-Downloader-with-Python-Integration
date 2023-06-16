package com.app.mp3downloader.data.repository

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.mp3downloader.common.DIRECTORY
import com.app.mp3downloader.common.FORMAT
import com.app.mp3downloader.common.MAX_QUALITY
import com.app.mp3downloader.common.VIDEO_URL
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloaderWorker @Inject constructor(
    private val context: Context,
    workerParams: WorkerParameters
) :
    Worker(context, workerParams) {
    lateinit var downloader: PyObject

    override fun doWork(): Result {
        val format = inputData.getString(FORMAT)
        val directory = inputData.getString(DIRECTORY)
        val maxQuality = inputData.getString(MAX_QUALITY)
        val videoUrl = inputData.getString(VIDEO_URL)
        val downloadDirectory = File(directory)
        var success: Boolean


        if (!downloadDirectory.exists()) {
            success = downloadDirectory.mkdirs()
            if (!success) {
                notifyFail()
                return Result.failure()
            }
        }

        val py = Python.getInstance()
        var pyf: PyObject

        var module = "yt-dlp"
        pyf = py.getModule("downloader")
        downloader = pyf.callAttr(
            "downloader",
            videoUrl,
            format,
            directory,
            maxQuality,
            Environment.getExternalStorageDirectory().path + "/Android/data/com.app.mp3downloader"
        )
        var download = downloader.callAttr("download")
        println("Started downloading")
        var processFail = false
        while (downloader.callAttr("state").toBoolean() != false || processFail) {
            processFail = downloader.callAttr("isFail").toBoolean()
//            val progress = downloader.callAttr("getProgress").toInt()
//            notifyProgress(progress)
            println("State : $processFail")
            if (processFail) {
                println("ERROR")
                notifyFail()
                return Result.failure()
            }
            try {
                TimeUnit.MILLISECONDS.sleep(600)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        println("State : $processFail")
        processFail = downloader.callAttr("isFail").toBoolean()
        if (!processFail) {
            notifySuccess()
            return Result.success()
        }
        if ((videoUrl?.contains("youtu.be") == true || videoUrl!!.contains("youtube.com")) && processFail && Build.VERSION.SDK_INT < 29) {
            pyf = py.getModule("ytb_downloader")
            module = "pytube"
            downloader = pyf.callAttr(
                "downloader",
                videoUrl,
                format,
                directory,
                maxQuality,
                Environment.getExternalStorageDirectory().path + "/Android/data/com.app.mp3downloader"
            )
            download = downloader.callAttr("download")
            println("Started downloading")
            while (downloader.callAttr("state").toBoolean() != false) {
                println("Value 2 " + downloader.get("status").toString())
                processFail = downloader.callAttr("isFail").toBoolean()
//                val progress = downloader.callAttr("getProgress").toInt()
//                notifyProgress(progress)
                if (processFail) {
                    notifyFail()
                    return Result.failure()
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(600)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        processFail = downloader.callAttr("isFail").toBoolean()

        if (!processFail && module == "pytube") {
            val ffmpeg = FFmpeg.getInstance(context)
            loadFFmpeg(ffmpeg)
            if (format == "m4a") {
                println("Converting")
                ffmpegExecute(
                    ffmpeg, downloader.callAttr("cmd").toJava<Array<String>>(
                        Array<String>::class.java
                    )
                )
            }
            notifySuccess()
            return Result.success()
        }
        notifyFail()
        return Result.failure()
    }
    private fun notifyProgress(progress: Int){
        println("notifyProgress " + progress)
    }
    override fun onStopped() {
        println("STOOOOOOP")
        downloader.put("stop", true)
        try {
            TimeUnit.MILLISECONDS.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        notifyStop()
        super.onStopped()
    }


    private fun notifyFail() {

    }


    private fun notifySuccess() {

    }

    private fun notifyStop() {

    }


    private fun loadFFmpeg(ffmpeg: FFmpeg) {
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onStart() {}
                override fun onFailure() {}
                override fun onSuccess() {}
                override fun onFinish() {}
            })
        } catch (e: FFmpegNotSupportedException) {
            Log.e("ERROR", e.toString())
        }
    }

    private fun ffmpegExecute(ffmpeg: FFmpeg, cmd: Array<String>) {
        try {
            ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}
                override fun onProgress(message: String) {
                    println("onProgress" + message)
                }

                override fun onFailure(message: String) {}
                override fun onSuccess(message: String) {}
                override fun onFinish() {
                    for (i in cmd.indices) {
                        if (cmd[i] == "-i") {
                            val filename = cmd[i + 1]
                            println(filename)
                            val tempfile = File(filename)
                            tempfile.delete()
                        }
                    }
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            Log.d("ERROR", e.toString())
        }
    }
}
