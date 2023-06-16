package com.app.mp3downloader.domain.usecase

data class UseCase(val grabVideoInfoUseCase: GrabVideoInfoUseCase,
                   val downloadVideoUseCase: DownloadVideoUseCase) {
}