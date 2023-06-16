package com.app.mp3downloader.presentation.initial

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.app.mp3downloader.R
import com.app.mp3downloader.common.*
import com.app.mp3downloader.data.model.YoutubeMetadata
import com.app.mp3downloader.domain.model.VideoDownloadParams
import com.app.mp3downloader.domain.usecase.UseCase
import com.app.mp3downloader.presentation.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InitialScreenViewModel @Inject constructor(private val useCase: UseCase,private val context: Context) : BaseViewModel() {

    lateinit var lifecycleOwner: LifecycleOwner
    val _navigateToDestination = MutableLiveData<Int>(0)
    lateinit var youtubeMetadata : YoutubeMetadata

    fun getDirectoryPathFromUri(uri: Uri): String? {
        val documentId = DocumentsContract.getTreeDocumentId(uri)
        return documentId?.let { documentId ->
            val parts = documentId.split(":")
            if (parts.size >= 2 && "primary".equals(parts[0], ignoreCase = true)) {
                val primaryVolume = Environment.getExternalStorageDirectory().path
                val subPath = parts[1]
                return "$primaryVolume/$subPath"
            }
            null
        }
    }
    fun onDownloadButtonClick(view: View,urlEditText:EditText,
                              destinationFolderEditText:EditText,
                              progressBar: ProgressBar){
        validateEditBoxes(urlEditText,destinationFolderEditText)
        //make it grab screen
        disableItems(view,urlEditText,destinationFolderEditText)
        progressBar.visibility = View.VISIBLE

        //Create an object of VideoDownloadParams
        val videoDownloadParams = VideoDownloadParams(urlEditText.text.toString(),
        "m4a",destinationFolderEditText.text.toString(),"")
        //Perform click event now
        useCase.grabVideoInfoUseCase(lifecycleOwner,videoDownloadParams){metadata ->
            progressBar.visibility = View.GONE
            enableItems(view,urlEditText,destinationFolderEditText)
            if(metadata == null){
                _navigateToDestination.value = ERROR_SCREEN
            }else{
                //move to grab screen
                this.youtubeMetadata = metadata
                _navigateToDestination.value = DOWNLOAD_SCREEN
            }
        }
    }
    private fun disableItems(view: View,urlEditText:EditText,
                             destinationFolderEditText:EditText){
        urlEditText.isEnabled = false
        destinationFolderEditText.isEnabled = false
        view.isEnabled = false
    }
    private fun enableItems(view: View,urlEditText:EditText,
                             destinationFolderEditText:EditText){
        view.isEnabled = true
        urlEditText.isEnabled = true
        destinationFolderEditText.isEnabled = true

    }
    private fun validateEditBoxes(urlEditText:EditText,destinationFolderEditText:EditText){
        val str = urlEditText.text.toString()
        println(str)
        if(urlEditText.isEmpty()){
            context.getString(R.string.emptyURL).showSnackbar(urlEditText)
            return
        }
        if(!urlEditText.isValidUrl()){
            context.getString(R.string.InvalidURL).showSnackbar(urlEditText)
            return
        }
        if(destinationFolderEditText.isEmpty()){
            context.getString(R.string.emptydis_et).showSnackbar(urlEditText)
            return
        }
        if(!destinationFolderEditText.isFolderPathValid()){
            context.getString(R.string.Invalid_dis_et).showSnackbar(urlEditText)
            return
        }
    }
}