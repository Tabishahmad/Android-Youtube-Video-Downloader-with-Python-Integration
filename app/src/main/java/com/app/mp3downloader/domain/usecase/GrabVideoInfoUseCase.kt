package com.app.mp3downloader.domain.usecase

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.app.mp3downloader.downloadHandler.DownloaderWorker
import com.app.mp3downloader.common.DIRECTORY
import com.app.mp3downloader.common.FORMAT
import com.app.mp3downloader.common.MAX_QUALITY
import com.app.mp3downloader.common.VIDEO_URL
import com.app.mp3downloader.domain.model.VideoDownloadParams
import javax.inject.Inject


class GrabVideoInfoUseCase @Inject constructor(val applicationContext: Context) {
    operator fun invoke(lifecycleOwner: LifecycleOwner,videoDownloadParams: VideoDownloadParams){
        initilizeDownloaderWorker(lifecycleOwner,videoDownloadParams)
    }
    private fun initilizeDownloaderWorker(lifecycleOwner: LifecycleOwner,
                                          videoDownloadParams: VideoDownloadParams){
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
                .observe(lifecycleOwner, Observer<WorkInfo>() {
                    @Override
                    fun onChanged(workInfo:WorkInfo){
                        println(" workInfo " + workInfo.progress)
                    }
                });
            downloaderWorkManager.enqueue(downloaderWorkRequest)
    }
}