//package com.app.mp3downloader
//
//import android.Manifest
//import android.app.Activity
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import android.provider.DocumentsContract
//import android.view.MotionEvent
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.Observer
//import androidx.work.Data
//import androidx.work.OneTimeWorkRequest
//import androidx.work.WorkInfo
//import androidx.work.WorkManager
//import com.chaquo.python.Python
//import com.chaquo.python.android.AndroidPlatform
//
//class FragmentContainerActivity : AppCompatActivity() {
//
//    private val REQUEST_DIRECTORY = 123
//    lateinit var etDestination: EditText
//    lateinit var urlText: EditText
//    lateinit var download: Button
//
//    val FORMAT = "FORMAT"
//    val DIRECTORY = "DIRECTORY"
//    val MAX_QUALITY = "MAX_QUALITY"
//    val VIDEO_URL = "VIDEO_URL"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//        urlText = findViewById(R.id.youTubeURL)
//        etDestination = findViewById(R.id.etDestination)
//        etDestination.setOnTouchListener { v, event ->
//            // Check if the touch event occurred on the drawableStart area
//            if (event.action == MotionEvent.ACTION_UP) {
//                val drawableStartWidth = etDestination.compoundPaddingStart
//                if (event.rawX <= drawableStartWidth) {
//                    // Handle the click event on drawableStart here
//                    // Perform your desired action
//                    openDirectoryPicker()
//                    return@setOnTouchListener true  // Consume the touch event
//                }
//            }
//            return@setOnTouchListener false  // Let the system handle the touch event
//        }
//        download = findViewById(R.id.btnDownload)
//        download.setOnClickListener(View.OnClickListener {
//            downloadAudio()
//        })
//
//
//    }
//
//    private fun downloadAudio() {
//        if (ContextCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            val videoUrl: String = urlText.getText().toString()
//            val directory: String = etDestination.getText().toString()
//            val format = "m4a"
//            val maxQuality = ""
//            val me: LifecycleOwner = this
//
//            val arguments = Data.Builder()
//                .putString(FORMAT, format)
//                .putString(DIRECTORY, directory)
//                .putString(MAX_QUALITY, maxQuality)
//                .putString(VIDEO_URL, videoUrl).build()
//
//            val downloaderWorkRequest = OneTimeWorkRequest.Builder(
//                DownloaderWorker::class.java
//            ).setInputData(arguments).build()
//            val downloaderWorkManager = WorkManager.getInstance(applicationContext)
//            WorkManager.getInstance(applicationContext)
//                .getWorkInfoByIdLiveData(downloaderWorkRequest.id)
//                .observe(me, Observer<WorkInfo>() {
//                    @Override
//                    fun onChanged(workInfo:WorkInfo){
//                        println(" workInfo " + workInfo.progress)
//                    }
//                });
//            downloaderWorkManager.enqueue(downloaderWorkRequest)
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                this@FragmentContainerActivity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//        ) {
//            AlertDialog.Builder(this@FragmentContainerActivity)
//                .setTitle(R.string.title_permission_needed)
//                .setMessage(R.string.text_why_permission)
//                .setPositiveButton("Ok",
//                    DialogInterface.OnClickListener { dialogInterface, i -> requestStoragePermission() })
//                .setNegativeButton(R.string.text_cancel,
//                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
//                .create().show()
//        } else {
//            requestStoragePermission()
//        }
//    }
//
//    rivate fun openDirectoryPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        intent.addCategory(Intent.CATEGORY_DEFAULT)
//        startActivityForResult(intent, REQUEST_DIRECTORY)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_DIRECTORY && resultCode == Activity.RESULT_OK) {
//            data?.data?.let { uri ->
//                val selectedDirectoryPath = getDirectoryPathFromUri(uri)
//                etDestination.setText(selectedDirectoryPath)
//                println("selectedDirectoryPath " + selectedDirectoryPath)
//            }
//        }
//    }
//
//    private fun getDirectoryPathFromUri(uri: Uri): String? {
//        val documentId = DocumentsContract.getTreeDocumentId(uri)
//        return documentId?.let { documentId ->
//            val parts = documentId.split(":")
//            if (parts.size >= 2 && "primary".equals(parts[0], ignoreCase = true)) {
//                val primaryVolume = Environment.getExternalStorageDirectory().path
//                val subPath = parts[1]
//                return "$primaryVolume/$subPath"
//            }
//            null
//        }
//    }
//
//
//    private fun requestStoragePermission() {
//        ActivityCompat.requestPermissions(
//            this@FragmentContainerActivity,
//            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//            1
//        )
//    }
//
//
//}