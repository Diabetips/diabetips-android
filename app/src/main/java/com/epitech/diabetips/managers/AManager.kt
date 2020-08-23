package com.epitech.diabetips.managers

import android.content.Context

abstract class AManager(protected val preference : String, protected val key : String = "") {

    protected fun saveString(context: Context, string: String, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
                .edit().putString(prefKey, string).apply()
    }

    protected fun saveInt(context: Context, int: Int, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
            .edit().putInt(prefKey, int).apply()
    }

    protected fun getString(context: Context, prefKey: String = key): String {
        return context.getSharedPreferences(preference, 0)
                .getString(prefKey, "")?: ""
    }

    protected fun getInt(context: Context, prefKey: String = key, defaultValue: Int = 0): Int {
        return context.getSharedPreferences(preference, 0)
            .getInt(prefKey, defaultValue)
    }

    protected fun removePreferenceKey(context: Context, prefKey: String = key) {
        context.getSharedPreferences(preference, 0)
            .edit().remove(prefKey).apply()
    }

    fun removePreferences(context: Context) {
        context.getSharedPreferences(preference, 0)
            .edit().clear().apply()
    }

}