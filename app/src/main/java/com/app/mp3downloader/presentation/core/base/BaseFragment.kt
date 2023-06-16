package com.app.mp3downloader.presentation.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.mp3downloader.BR
import kotlinx.coroutines.launch

abstract class BaseFragment<vModel : BaseViewModel, viewDataBinding : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
    ): Fragment() {
    protected abstract val viewModel: vModel?

    protected lateinit var binding: viewDataBinding
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, layoutId, container, false);
        binding.setVariable(BR.viewModel,viewModel)
        init()
        observeViewModel()
        return binding.root
    }
    open fun init(){}
    protected abstract fun observeViewModel()
    protected fun performCoroutineTask(block: suspend  () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch() {
            block()
        }
    }
}