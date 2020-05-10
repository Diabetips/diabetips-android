package com.epitech.diabetips.managers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ModeManager : AManager("mode_preference") {

    private val DARK_MODE : String = "dark_mode"

    private object Holder {
        val INSTANCE = ModeManager()
    }

    companion object {
        val instance: ModeManager by lazy { Holder.INSTANCE }
    }

    fun saveDarkMode(context: Context, mode: Int) {
        saveInt(context, mode, DARK_MODE)
    }

    fun getDarkMode(context: Context) : Int {
        return getInt(context, DARK_MODE, AppCompatDelegate.getDefaultNightMode())
    }

}