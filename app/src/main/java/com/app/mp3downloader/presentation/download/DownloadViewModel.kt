package com.app.mp3downloader.presentation.download

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.app.mp3downloader.domain.model.VideoDownloadParams
import com.app.mp3downloader.domain.usecase.UseCase
import com.app.mp3downloader.presentation.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(private val useCase: UseCase): BaseViewModel() {
    lateinit var lifecycleOwner: LifecycleOwner
    fun downloadVideo(videoURL :String,directory:String){
        val videoDownloadParams = VideoDownloadParams(videoURL,
            "m4a",directory,"")
        useCase.downloadVideoUseCase(lifecycleOwner,videoDownloadParams){

        }
    }
}