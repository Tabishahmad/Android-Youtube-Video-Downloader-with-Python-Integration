package com.app.mp3downloader.data.repository.remote

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class VideoDownloader : AsyncTask<Void, Long, Boolean>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Void): Boolean {

        val client = OkHttpClient()
        val url = "http://myamazingvideo.mp4"
        val call = client.newCall(Request.Builder().url(url).get().build())

        try {
            val response = call.execute()
            if (response.code == 200 || response.code == 201) {

                val responseHeaders = response.headers
                for (i in 0 until responseHeaders.size) {
//                    Log.d(LOG_TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i))
                }

                var inputStream: InputStream? = null
                try {
                    inputStream = response.body!!.byteStream()

                    val buff = ByteArray(1024 * 4)
                    var downloaded: Long = 0
                    val target = response.body!!.contentLength()
                    val mediaFile = ""//File(requireActivity().cacheDir, "mySuperVideo.mp4")
                    val output = FileOutputStream(mediaFile)

                    publishProgress(0L, target)
                    while (true) {
                        val readed = inputStream.read(buff)

                        if (readed == -1) {
                            break
                        }
                        output.write(buff, 0, readed)
                        //write buff
                        downloaded += readed.toLong()
                        publishProgress(downloaded, target)
                        if (isCancelled) {
                            return false
                        }
                    }

                    output.flush()
                    output.close()

                    return downloaded == target
                } catch (ignore: IOException) {
                    return false
                } finally {
                    inputStream?.close()
                }
            } else {
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    override fun onProgressUpdate(vararg values: Long?) {
        super.onProgressUpdate(values[0])
//        progressBar.max = values[1].toInt()
//        progressBar.progress = values[0].toInt()
    }

    override fun onPostExecute(aBoolean: Boolean) {
        super.onPostExecute(aBoolean)

//        progressBar.visibility = View.GONE
//
//        if (mediaFile != null && mediaFile.exists()) {
//            playVideo()
//        }
    }
}
