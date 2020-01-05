package com.epitech.diabetips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(), me.ibrahimsn.lib.OnItemSelectedListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        smoothBottomBaBar.setOnItemSelectedListener(this)
        onItemSelect(0)
    }

    private fun loadFragment(fragment: Fragment?) : Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navigationFragment, fragment)
                .commit()
            return true
        }
        return false

    }

    override fun onItemSelect(pos: Int) {
        when (pos) {
            0 -> loadFragment(HomeFragment())
            1 -> loadFragment(ProfileFragment())
            2 -> loadFragment(SettingsFragment())
        }
    }

}
