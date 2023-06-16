package com.app.mp3downloader.presentation.initial

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import com.app.mp3downloader.R
import com.app.mp3downloader.presentation.core.base.BaseFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.mp3downloader.common.DOWNLOAD_SCREEN
import com.app.mp3downloader.common.ERROR_SCREEN
import com.app.mp3downloader.databinding.FragmentInatialScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitialScreenFragment : BaseFragment<InitialScreenViewModel,FragmentInatialScreenBinding>(
R.layout.fragment_inatial_screen
) {
    private val REQUEST_DIRECTORY = 123
    override val viewModel: InitialScreenViewModel by viewModels()

    override fun observeViewModel() {
        viewModel._navigateToDestination.observe(this){destination->
            when(destination){
                DOWNLOAD_SCREEN ->{
                    viewModel._navigateToDestination.value = 0
                    val b = Bundle()
                    b.putSerializable("youtubeMetadata",viewModel.youtubeMetadata)
                    b.putString("videoURL",binding.youTubeURL.text.toString())
                    b.putString("directory",binding.folderDestination.text.toString())
                    findNavController().navigate(R.id.downloadScreen,b)
                }
                ERROR_SCREEN->{

                }
            }
        }
    }
    override fun init() {
        viewModel.lifecycleOwner = this
        handleDestinationFolderClickEvent()
    }
    private fun handleDestinationFolderClickEvent(){
        binding.folderDestination.setOnTouchListener { v, event ->
            val DRAWABLE_START = 0
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX <= (binding.folderDestination.left + binding.folderDestination.compoundDrawables[DRAWABLE_START].bounds.width())) {
                    // Handle click on the start drawable
                    openDirectoryPicker()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }
    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(intent, REQUEST_DIRECTORY)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DIRECTORY && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val selectedDirectoryPath = viewModel.getDirectoryPathFromUri(uri)
                binding.folderDestination.setText(selectedDirectoryPath)
                println("selectedDirectoryPath " + selectedDirectoryPath)
            }
        }
    }
}