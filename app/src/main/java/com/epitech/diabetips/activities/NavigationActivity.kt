package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.fragments.DashboardFragment
import com.epitech.diabetips.fragments.HomeFragment
import com.epitech.diabetips.fragments.ProfileFragment
import com.epitech.diabetips.fragments.RecipeFragment
import com.epitech.diabetips.services.NfcReaderService
import com.epitech.diabetips.services.PENDING_INTENT_TECH_DISCOVERED
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.ANavigationFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : ADiabetipsActivity(R.layout.activity_navigation), me.ibrahimsn.lib.OnItemSelectedListener  {

    private var currentFragment: Fragment? = null
    var nfcReader: NfcReaderService? = null

    companion object {
        var defaultFragmentSelect = ANavigationFragment.FragmentType.HOME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smoothBottomBaBar.onItemSelectedListener = this
        selectDefaultFragment()
        nfcReader = NfcReaderService(this, intent, this) {
            nfcReaderUpdated()
        }
    }

    private fun nfcReaderUpdated() {
        if (currentFragment != null &&
            (currentFragment as ANavigationFragment).fragmentType == ANavigationFragment.FragmentType.HOME)
            (currentFragment as HomeFragment).entriesManager.getItems()
    }

    private fun selectDefaultFragment() {
        smoothBottomBaBar.itemActiveIndex = defaultFragmentSelect.ordinal
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

    override fun onItemSelect(pos: Int) : Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.navigationFragment) as ANavigationFragment?
        if (fragment != null && fragment.isLoading()) {
            smoothBottomBaBar.itemActiveIndex = fragment.fragmentType.ordinal
            return false
        }
        when (pos) {
            ANavigationFragment.FragmentType.HOME.ordinal -> return loadFragment(HomeFragment())
            ANavigationFragment.FragmentType.DASHBOARD.ordinal -> return loadFragment(DashboardFragment())
            ANavigationFragment.FragmentType.RECIPES.ordinal -> return loadFragment(RecipeFragment())
            ANavigationFragment.FragmentType.PROFILE.ordinal -> return loadFragment(ProfileFragment())
            ANavigationFragment.FragmentType.SETTINGS.ordinal -> return loadFragment(null)
        }
        return false
    }

    fun setDefaultFragmentSelect(navigationFragment: ANavigationFragment.FragmentType = ANavigationFragment.FragmentType.HOME) {
        defaultFragmentSelect = navigationFragment
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.navigationFragment) as ANavigationFragment?
        if (fragment != null && fragment.isLoading()) {
            return
        } else if (smoothBottomBaBar.itemActiveIndex != defaultFragmentSelect.ordinal) {
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
