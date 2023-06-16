package com.app.mp3downloader.domain.usecase

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.app.mp3downloader.R
import com.app.mp3downloader.common.*
import com.app.mp3downloader.data.model.YoutubeMetadata
import com.app.mp3downloader.data.repository.DownloaderWorker
import com.app.mp3downloader.domain.model.VideoDownloadParams
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(val applicationContext: Context){
    operator fun invoke(
        lifecycleOwner: LifecycleOwner,
        videoDownloadParams: VideoDownloadParams,
        callback: (YoutubeMetadata?) -> Unit
    ) {
        initilizeDownloaderWorker(lifecycleOwner,videoDownloadParams)
    }
    private fun initilizeDownloaderWorker(lifecycleOwner: LifecycleOwner,
                                          videoDownloadParams: VideoDownloadParams
    ){
        val arguments = Data.Builder()
            .putString(FORMAT, videoDownloadParams.format)
            .putString(DIRECTORY, videoDownloadParams.directory)
            .putString(MAX_QUALITY, videoDownloadParams.maxQuality)
            .putString(VIDEO_URL, videoDownloadParams.videoUrl).build()
        val downloaderWorkRequest = OneTimeWorkRequest.Builder(
            DownloaderWorker::class.java
        ).setInputData(arguments).build()

        val downloaderWorkManager = WorkManager.getInstance(applicationContext)

        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(downloaderWorkRequest.id)
            .observe(lifecycleOwner, Observer<WorkInfo>() {workInfo->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    applicationContext.getString(R.string.file_downloaded_successfully).showCustomToast(applicationContext,
                    Toast.LENGTH_LONG)
                }
            });
        downloaderWorkManager.enqueue(downloaderWorkRequest)
    }
}