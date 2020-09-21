package com.epitech.diabetips.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.services.NotificationService
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.ANavigationFragment
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : ADiabetipsActivity(R.layout.activity_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkModeSwitch.isChecked = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        darkModeSwitch.setOnCheckedChangeListener { _, state ->
            if (state) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            NavigationActivity.defaultFragmentSelect = ANavigationFragment.FragmentType.PROFILE
            ModeManager.instance.saveDarkMode(this, AppCompatDelegate.getDefaultNightMode())
            recreate()
        }
        testNotificationButton.visibility = if (getString(R.string.api_base_url).contains("dev")) View.VISIBLE else View.GONE
        testNotificationButton.setOnClickListener {
            NotificationService.instance.test().doOnSuccess {
                if (it.second.component2() == null) {
                    Toast.makeText(this, getString(R.string.test_notification_send), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
        closeSettingsButton.setOnClickListener { finish() }
    }
}
