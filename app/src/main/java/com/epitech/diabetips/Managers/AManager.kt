package com.epitech.diabetips.Managers

import android.annotation.SuppressLint
import android.content.Context

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class AManager(private val preference : String, private val key : String = "") {

    @SuppressLint("ApplySharedPref")
    fun saveString(context: Context, string: String, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
                .edit().putString(prefKey, string).commit()
    }

    fun getString(context: Context, prefKey: String = key): String {
        return context.getSharedPreferences(preference, 0)
                .getString(prefKey, "")
    }

    @SuppressLint("ApplySharedPref")
    fun removeString(context: Context, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
            .edit().remove(prefKey).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun removePreference(context: Context) {
        context.getSharedPreferences(preference, 0)
            .edit().clear().commit()
    }

}