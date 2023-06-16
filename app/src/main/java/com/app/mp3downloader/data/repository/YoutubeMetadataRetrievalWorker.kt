package com.app.mp3downloader.data.repository

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.app.mp3downloader.common.DIRECTORY
import com.app.mp3downloader.common.FORMAT
import com.app.mp3downloader.common.MAX_QUALITY
import com.app.mp3downloader.common.VIDEO_URL
import com.app.mp3downloader.data.model.YoutubeMetadata
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.google.gson.Gson
import javax.inject.Inject

class YoutubeMetadataRetrievalWorker @Inject
    constructor(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    lateinit var downloader: PyObject

    override suspend fun doWork(): Result {
        val format = inputData.getString(FORMAT)
        val directory = inputData.getString(DIRECTORY)
        val maxQuality = inputData.getString(MAX_QUALITY)
        val videoUrl = inputData.getString(VIDEO_URL)

        val py = Python.getInstance()
        var pyf: PyObject

        pyf = py.getModule("downloader")
        downloader = pyf.callAttr("downloader", videoUrl, format, directory, maxQuality,
            Environment.getExternalStorageDirectory().path + "/Android/data/com.app.mp3downloader")

        val videoInfo: String = downloader.callAttr("get_video_info").toString()
        val gson = Gson()
        val myData = gson.fromJson(videoInfo, YoutubeMetadata::class.java)
        println(myData)
        val customObjectJson = gson.toJson(myData)

        val outputData = Data.Builder()
            .putString("customObjectJson", customObjectJson)
            .build()
        return Result.success(outputData)
    }
}
