package com.app.mp3downloader.domain.model

data class VideoDownloadParams(
    val videoUrl: String,
    val format: String,
    val directory: String,
    val maxQuality: String
)
