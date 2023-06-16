package com.app.mp3downloader.data.model
import java.io.Serializable

data class YoutubeMetadata(val title: String,
                           val thumbnail: String,
                           val view_count: String,
                           val like_count: String,): Serializable
