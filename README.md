# Android YouTube Downloader — Python Integration

An Android app that downloads YouTube videos by embedding **Python scripts directly in the app** via Chaquopy, instead of relying on a server-side backend — fetching metadata and running the download as a background `WorkManager` job.

## Features
- Fetch video metadata (title, formats) by calling a Python script from Kotlin via Chaquopy
- Background download via `WorkManager`, with a stop/cancel action
- Progress and download state surfaced through a Clean Architecture use-case layer

## Architecture
Clean Architecture + MVVM:
```
presentation/   → Fragments, ViewModels
domain/         → UseCases (GrabVideoInfoUseCase, DownloadVideoUseCase)
data/           → Repository impls, WorkManager workers, Python bridge
```
- **DI:** Hilt
- **Python bridge:** Chaquopy (`app/src/main/python`)

## Tech stack
`Kotlin` `Chaquopy (Python on Android)` `WorkManager` `Hilt` `Clean Architecture`

## Running it
```bash
git clone https://github.com/Tabishahmad/Android-Youtube-Video-Downloader-with-Python-Integration.git
```
Open in Android Studio and run.

## Write-up
Full walkthrough: [Leveraging Python Scripts in Android: A Guide to YouTube Video Downloads](https://medium.com/@tabish.dev.work/how-to-use-python-script-in-android-d064081ebc8c)

## Disclaimer
Built for educational purposes. Respect YouTube's terms of service and copyright law when using this.
