package com.epitech.diabetips.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R

abstract class ADiabetipsActivity(private val layoutId: Int) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
    }

}