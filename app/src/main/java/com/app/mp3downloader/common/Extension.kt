package com.app.mp3downloader.common

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.app.mp3downloader.R
import com.bumptech.glide.Glide
import android.util.Patterns
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import java.io.File


fun ImageView.loadImageWithGlide(context: Context, imageURL: String) {
    Glide.with(context)
        .load(imageURL)
        .thumbnail(Glide.with(context).load(R.raw.load))
        .into(this)
}

fun String.showCustomToast(context: Context, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, this, duration).show()
}
fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}
fun EditText.isValidUrl(): Boolean {
    val url = this.text.toString().trim()
    return Patterns.WEB_URL.matcher(url).matches()
}
fun EditText.isEmpty(): Boolean {
    return text.toString().trim().isEmpty()
}
fun String.showSnackbar(view: View) {
    Snackbar.make(view, this, Snackbar.LENGTH_SHORT).show()
}
fun EditText.isFolderPathValid(): Boolean {
    val path = this.text.toString().trim()
    val folder = File(path)
    return folder.exists() && folder.isDirectory
}
