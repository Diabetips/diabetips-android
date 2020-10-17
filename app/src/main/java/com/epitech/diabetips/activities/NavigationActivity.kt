package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.epitech.diabetips.R
import com.epitech.diabetips.fragments.DashboardFragment
import com.epitech.diabetips.fragments.HomeFragment
import com.epitech.diabetips.fragments.ProfileFragment
import com.epitech.diabetips.fragments.RecipeFragment
import com.epitech.diabetips.managers.FavoriteManager
import com.epitech.diabetips.services.NfcReaderService
import com.epitech.diabetips.services.NotificationService
import com.epitech.diabetips.storages.FCMTokenObject
import com.epitech.diabetips.storages.NotificationObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.ANavigationFragment
import com.epitech.diabetips.utils.RequestCode
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : ADiabetipsActivity(R.layout.activity_navigation), me.ibrahimsn.lib.OnItemSelectedListener  {

    private var currentFragment: Fragment? = null
    var nfcReader: NfcReaderService? = null

    companion object {
        var defaultFragmentSelect = ANavigationFragment.FragmentType.HOME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smoothBottomBar.onItemSelectedListener = this
        selectDefaultFragment()
        initFirebase()
        nfcReader = NfcReaderService(this, intent, this) {
            nfcReaderUpdated()
        }
        FavoriteManager.instance.init(this)
    }

    private fun initFirebase() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                Log.d("FirebaseToken", "${task.result?.token}")
                if (task.result?.token != null)
                    NotificationService.instance.register(FCMTokenObject(task.result?.token!!)).subscribe()
                NotificationService.instance.getAll<NotificationObject>(PaginationObject(1, resources.getInteger(R.integer.pagination_default))).doOnSuccess {
                    if (it.second.component2() == null && it.second.component1()?.firstOrNull() != null && !it.second.component1()?.first()!!.read) {
                        Log.d("FirebaseNotification", it.second.component1()!!.first().toString())
                        it.second.component1()!!.first().send(this)
                    }
                }.subscribe()
            })
    }

    private fun nfcReaderUpdated() {
        if (currentFragment != null && (currentFragment as ANavigationFragment).fragmentType == ANavigationFragment.FragmentType.HOME) {
            (currentFragment as HomeFragment).onNfc()
        }
    }

    private fun selectDefaultFragment() {
        smoothBottomBar.itemActiveIndex = defaultFragmentSelect.ordinal
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
            smoothBottomBar.itemActiveIndex = fragment.fragmentType.ordinal
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

    private fun setDefaultFragmentSelect(navigationFragment: ANavigationFragment.FragmentType = ANavigationFragment.FragmentType.HOME) {
        defaultFragmentSelect = navigationFragment
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.navigationFragment) as ANavigationFragment?
        if (fragment != null && fragment.isLoading()) {
            return
        } else if (smoothBottomBar.itemActiveIndex != defaultFragmentSelect.ordinal) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.NFC_READER.ordinal -> nfcReader!!.onNewIntent(data)
            else -> (supportFragmentManager.findFragmentById(R.id.navigationFragment) as ANavigationFragment?)?.onActivityResult(requestCode, resultCode, data)
        }
    }

}
