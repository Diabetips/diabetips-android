package com.epitech.diabetips.Managers

import android.annotation.SuppressLint
import android.content.Context

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class AManager(private val preference : String, private val key : String) {

    @SuppressLint("ApplySharedPref")
    fun saveString(context: Context, string: String) {
        context.getSharedPreferences(preference, 0)
                .edit().putString(key, string).commit()
    }

    fun getString(context: Context): String {
        return context.getSharedPreferences(preference, 0)
                .getString(key, "")
    }

    @SuppressLint("ApplySharedPref")
    fun removeString(context: Context) {
        context.getSharedPreferences(preference, 0)
            .edit().remove(key).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun removePreference(context: Context) {
        context.getSharedPreferences(preference, 0)
            .edit().clear().commit()
    }

}