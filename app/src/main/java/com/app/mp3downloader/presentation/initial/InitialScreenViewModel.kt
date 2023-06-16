package com.app.mp3downloader.presentation.initial

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.app.mp3downloader.R
import com.app.mp3downloader.common.isEmpty
import com.app.mp3downloader.common.isFolderPathValid
import com.app.mp3downloader.common.isValidUrl
import com.app.mp3downloader.common.showSnackbar
import com.app.mp3downloader.domain.model.VideoDownloadParams
import com.app.mp3downloader.domain.usecase.UseCase
import com.app.mp3downloader.presentation.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InitialScreenViewModel @Inject constructor(private val useCase: UseCase,private val context: Context) : BaseViewModel() {

    lateinit var lifecycleOwner: LifecycleOwner

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
    fun onDownloadButtonClick(urlEditText:EditText,destinationFolderEditText:EditText){
        validateEditBoxes(urlEditText,destinationFolderEditText)

        //Create an object of VideoDownloadParams
        val videoDownloadParams = VideoDownloadParams(urlEditText.text.toString(),
        "m4a",destinationFolderEditText.text.toString(),"")
        //Perform click event now
        useCase.grabVideoInfoUseCase(lifecycleOwner,videoDownloadParams)
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