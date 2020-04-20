package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.services.NfcReaderService
import com.epitech.diabetips.services.PENDING_INTENT_TECH_DISCOVERED
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(), me.ibrahimsn.lib.OnItemSelectedListener  {

    var currentFragment: Fragment? = null
    private var nfcReader: NfcReaderService? = null
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
        selectDefaultFragment()
        nfcReader = NfcReaderService(this, intent)
    }

    private fun selectDefaultFragment() {
        smoothBottomBaBar.setActiveItem(defaultFragmentSelect.ordinal)
        onItemSelect(defaultFragmentSelect.ordinal)
        setDefaultFragmentSelect()
    }

    private fun loadFragment(fragment: Fragment?) : Boolean {
        if (fragment != null) {
            currentFragment = fragment
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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavigationFragment?
        if (fragment != null && fragment.isLoading()) {
            return
        } else if (smoothBottomBaBar.getActiveItem() != defaultFragmentSelect.ordinal) {
            selectDefaultFragment()
        } else {
            moveTaskToBack(true)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcReader!!.onResume()
    }

    override fun onPause() {
        nfcReader!!.onPause()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        nfcReader!!.onNewIntent(intent)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PENDING_INTENT_TECH_DISCOVERED -> nfcReader!!.onNewIntent(data)
        }
    }

}
