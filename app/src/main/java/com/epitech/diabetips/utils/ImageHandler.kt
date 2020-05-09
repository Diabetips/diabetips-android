package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.ImageView
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.Signature

class ImageHandler {

    private object Holder { val INSTANCE = ImageHandler() }

    companion object {
        val instance: ImageHandler by lazy { Holder.INSTANCE }
    }

    private val options = RequestOptions()
        .fitCenter()
        .priority(Priority.HIGH)

    fun loadImage(image: ImageView, context: Context, url: String, placeholder: Int, cacheImage: Boolean = true) {
        Glide.with(context)
            .load(url)
            .apply(options)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(if (cacheImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
            .skipMemoryCache(!cacheImage)
            .into(image)
    }

    fun encodeImage(_bitmap: Bitmap, maxWidthAndHeight: Int? = null): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var bitmap = _bitmap
        if (maxWidthAndHeight != null) {
            if (bitmap.width > bitmap.height){
                bitmap = Bitmap.createBitmap(bitmap, bitmap.width / 2 - bitmap.height / 2, 0, bitmap.height, bitmap.height)
            } else {
                bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.height / 2 - bitmap.width / 2, bitmap.width, bitmap.width)
            }
            bitmap = bitmap.scale(maxWidthAndHeight, maxWidthAndHeight)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}