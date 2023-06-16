package com.app.mp3downloader.hilt

import android.content.Context
import com.app.mp3downloader.domain.usecase.DownloadVideoUseCase
import com.app.mp3downloader.domain.usecase.GrabVideoInfoUseCase
import com.app.mp3downloader.domain.usecase.UseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideBookUseCase(@ApplicationContext appContext: Context):UseCase{
        return UseCase(GrabVideoInfoUseCase(appContext),
            DownloadVideoUseCase())
    }
}