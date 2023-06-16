package com.app.mp3downloader.domain.usecase

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.app.mp3downloader.common.DIRECTORY
import com.app.mp3downloader.common.FORMAT
import com.app.mp3downloader.common.MAX_QUALITY
import com.app.mp3downloader.common.VIDEO_URL
import com.app.mp3downloader.data.model.YoutubeMetadata
import com.app.mp3downloader.data.repository.YoutubeMetadataRetrievalWorker
import com.app.mp3downloader.domain.model.VideoDownloadParams
import com.google.gson.Gson
import javax.inject.Inject


class GrabVideoInfoUseCase @Inject constructor(val applicationContext: Context) {
    operator fun invoke(
        lifecycleOwner: LifecycleOwner,
        videoDownloadParams: VideoDownloadParams,
        callback: (YoutubeMetadata?) -> Unit
    ) {
        grabVideoInfo(lifecycleOwner, videoDownloadParams) { youtubeMetadata ->
            callback(youtubeMetadata)
        }
    }

    private fun grabVideoInfo(
        lifecycleOwner: LifecycleOwner,
        videoDownloadParams: VideoDownloadParams,
        callback: (YoutubeMetadata?) -> Unit
    ) {
        val arguments = Data.Builder()
            .putString(FORMAT, videoDownloadParams.format)
            .putString(DIRECTORY, videoDownloadParams.directory)
            .putString(MAX_QUALITY, videoDownloadParams.maxQuality)
            .putString(VIDEO_URL, videoDownloadParams.videoUrl)
            .build()

        val downloaderWorkRequest = OneTimeWorkRequest.Builder(
            YoutubeMetadataRetrievalWorker::class.java
        ).setInputData(arguments).build()

        val workManager = WorkManager.getInstance(applicationContext)
        val workInfoLiveData = workManager.getWorkInfoByIdLiveData(downloaderWorkRequest.id)

        workInfoLiveData.observe(lifecycleOwner, Observer { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                val outputData = workInfo.outputData
                val customObjectJson = outputData.getString("customObjectJson")
                if (customObjectJson != null) {
                    try {
                        val gson = Gson()
                        val youtubeMetadata: YoutubeMetadata? = gson.fromJson(
                            customObjectJson,
                            YoutubeMetadata::class.java
                        )
                        callback(youtubeMetadata)
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
        workManager.enqueue(downloaderWorkRequest)
    }


}