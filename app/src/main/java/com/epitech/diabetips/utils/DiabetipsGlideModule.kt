package com.epitech.diabetips.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

@GlideModule
class DiabetipsGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply { RequestOptions().fitCenter().priority(Priority.HIGH).signature(ObjectKey(System.currentTimeMillis().toShort())) }
    }
}