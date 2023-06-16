package com.app.mp3downloader.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.mp3downloader.R
import com.app.mp3downloader.databinding.ActivityFragmentContainerBinding
import com.app.mp3downloader.presentation.core.base.BaseActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentContainerActivity : BaseActivity<ActivityFragmentContainerBinding>(R.layout.activity_fragment_container) {
    override fun init() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}