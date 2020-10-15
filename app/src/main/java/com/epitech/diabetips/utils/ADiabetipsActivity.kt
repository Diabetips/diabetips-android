package com.epitech.diabetips.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.ModeManager

abstract class ADiabetipsActivity(private val layoutId: Int) : AppCompatActivity() {

    private var lastDarkMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        lastDarkMode = ModeManager.instance.getDarkMode(this)
        changeTheme(false)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        MaterialHandler.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
    }

    protected fun changeTheme(refreshView: Boolean = true) {
        if (lastDarkMode == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        if (refreshView) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        if (lastDarkMode != ModeManager.instance.getDarkMode(this)) {
            lastDarkMode = ModeManager.instance.getDarkMode(this)
            changeTheme()
        }
    }

}