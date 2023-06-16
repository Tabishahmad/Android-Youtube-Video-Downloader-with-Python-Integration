package com.app.mp3downloader.presentation.download

import androidx.fragment.app.viewModels
import com.app.mp3downloader.R
import com.app.mp3downloader.common.loadImageWithGlide
import com.app.mp3downloader.data.model.YoutubeMetadata
import com.app.mp3downloader.databinding.FragmentDownloadBinding
import com.app.mp3downloader.presentation.core.base.BaseFragment
import com.app.mp3downloader.utli.NumberFormatHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadFragment : BaseFragment<DownloadViewModel, FragmentDownloadBinding>(
    R.layout.fragment_download
) {
    override val viewModel: DownloadViewModel by viewModels()
    lateinit var youtubeMetadata : YoutubeMetadata
    override fun observeViewModel() {

    }

    override fun init() {
        viewModel.lifecycleOwner = this
        val args = arguments
        if (args != null) {
            youtubeMetadata = (args.getSerializable("youtubeMetadata") as? YoutubeMetadata)!!
            val videoURL = args.getString("videoURL")
            val directory = args.getString("directory")
            if (videoURL != null && directory != null) {
                viewModel.downloadVideo(videoURL,directory)
            }
        }
        binding.cardImage.loadImageWithGlide(requireContext(),youtubeMetadata.thumbnail)
        val view_count = youtubeMetadata.view_count.toLongOrNull()
        binding.viewCount.setText(view_count?.let { NumberFormatHelper.compactDecimalFormat(it) } + " Views")

        val like_count = youtubeMetadata.like_count.toLongOrNull()
        binding.likeCount.setText(like_count?.let { NumberFormatHelper.compactDecimalFormat(it) } + " Likes")
        binding.videoTitle.setText(youtubeMetadata.title)
    }
}
