package com.epitech.diabetips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.activity_navigation.view.*

class NavigationActivity : AppCompatActivity(), me.ibrahimsn.lib.OnItemSelectedListener  {

    companion object {
        var defaultFragmentSelect = NavigationFragment.FragmentType.HOME
    }

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
        smoothBottomBaBar.setActiveItem(defaultFragmentSelect.ordinal)
        onItemSelect(defaultFragmentSelect.ordinal)
        setDefaultFragmentSelect()
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
        val fragment = supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavigationFragment?
        if (fragment != null && fragment.isLoading()) {
            smoothBottomBaBar.setActiveItem(fragment.fragmentType.ordinal)
            return
        }
        when (pos) {
            NavigationFragment.FragmentType.HOME.ordinal -> loadFragment(HomeFragment())
            NavigationFragment.FragmentType.PROFILE.ordinal -> loadFragment(ProfileFragment())
            NavigationFragment.FragmentType.SETTINGS.ordinal -> loadFragment(SettingsFragment())
        }
    }

    fun setDefaultFragmentSelect(navigationFragment: NavigationFragment.FragmentType = NavigationFragment.FragmentType.HOME) {
        defaultFragmentSelect = navigationFragment
    }

}
