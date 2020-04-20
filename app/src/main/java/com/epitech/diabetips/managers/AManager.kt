package com.epitech.diabetips.managers

import android.annotation.SuppressLint
import android.content.Context

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class AManager(protected val preference : String, protected val key : String = "") {

    @SuppressLint("ApplySharedPref")
    protected fun saveString(context: Context, string: String, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
                .edit().putString(prefKey, string).commit()
    }

    @SuppressLint("ApplySharedPref")
    protected fun saveInt(context: Context, int: Int, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
            .edit().putInt(prefKey, int).commit()
    }

    protected fun getString(context: Context, prefKey: String = key): String {
        return context.getSharedPreferences(preference, 0)
                .getString(prefKey, "")?: ""
    }

    protected fun getInt(context: Context, prefKey: String = key, defaultValue: Int = 0): Int {
        return context.getSharedPreferences(preference, 0)
            .getInt(prefKey, defaultValue)
    }

    @SuppressLint("ApplySharedPref")
    protected fun removePreferenceKey(context: Context, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
            .edit().remove(prefKey).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun removePreferences(context: Context) {
        context.getSharedPreferences(preference, 0)
            .edit().clear().commit()
    }

}