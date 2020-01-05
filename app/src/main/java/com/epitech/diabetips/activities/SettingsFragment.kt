package com.epitech.diabetips.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        view.darkModeSwitch.isChecked = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        view.darkModeSwitch.setOnCheckedChangeListener { _, state ->
            if (state) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            ModeManager.instance.saveDarkMode(context!!, AppCompatDelegate.getDefaultNightMode())
        }
        return view
    }
}
