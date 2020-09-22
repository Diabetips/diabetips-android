package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AuthManager
import com.github.kittinunf.fuel.core.FuelManager
import java.io.ByteArrayOutputStream


class ImageHandler {

    private object Holder { val INSTANCE = ImageHandler() }

    companion object {
        val instance: ImageHandler by lazy { Holder.INSTANCE }
    }

    private var header = LazyHeaders.Builder().build()

    private val options = RequestOptions()
        .fitCenter()
        .priority(Priority.HIGH)

    fun updateHeader(authorization: String) {
        header = LazyHeaders.Builder().addHeader("Authorization", "Bearer $authorization").build()
    }

    fun loadImage(image: ImageView, context: Context, url: String, placeholder: Int, cacheImage: Boolean = true, placeholderColor: Int = R.color.colorHint) {
        val drawable = ContextCompat.getDrawable(context, placeholder)
        drawable?.setTint(ContextCompat.getColor(context, placeholderColor))
        Glide.with(context)
            .load(GlideUrl(url, header))
            .apply(options)
            .placeholder(drawable)
            .error(drawable)
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