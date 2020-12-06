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
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.services.*
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.ANavigationFragment
import com.epitech.diabetips.utils.DialogHandler
import com.epitech.diabetips.utils.RequestCode
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : ADiabetipsActivity(R.layout.activity_navigation), me.ibrahimsn.lib.OnItemSelectedListener {
    var nfcReader: NfcReaderService? = null

    companion object {
        private var currentFragment: Fragment? = null
        private var unreadMessage: Boolean = false
        var defaultFragmentSelect = ANavigationFragment.FragmentType.HOME

        fun setUnreadMessage(value: Boolean = unreadMessage) {
            unreadMessage = value
            if ((currentFragment as ANavigationFragment?)?.fragmentType == ANavigationFragment.FragmentType.HOME) {
                (currentFragment as HomeFragment).updateChatIcon()
            }
        }

        fun getUnreadMessage(): Boolean {
            return unreadMessage
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smoothBottomBar.onItemSelectedListener = this
        selectDefaultFragment()
        initFirebase()
        updateChatUser()
        nfcReader = NfcReaderService(this, intent, this) {
            nfcReaderUpdated()
        }
        FavoriteManager.instance.init(this)
    }

    private fun initFirebase() {
        var notification = NotificationObject()
        if (intent.hasExtra(getString(R.string.param_notification))) {
            notification = (intent.getSerializableExtra(getString(R.string.param_notification)) as NotificationObject)
            handleNotification(notification)
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Firebase", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            Log.d("FirebaseToken", "${task.result}")
            if (task.result != null)
                NotificationService.instance.register(FCMTokenObject(task.result!!)).subscribe()
            NotificationService.instance.getAll<NotificationObject>(PaginationObject(1, resources.getInteger(R.integer.pagination_default))).doOnSuccess {
                if (it.second.component2() == null) {
                    it.second.component1()?.forEach { notif ->
                        if (notif.id != notification.id) {
                            handleNotification(notif)
                        }
                    }
                }
            }.subscribe()
        })
    }

    private fun handleNotification(notification: NotificationObject?) {
        if (notification?.read == false && notification.id.isNotEmpty()) {
            when (notification.type) {
                NotificationObject.Type.user_invite.name -> {
                    DialogHandler.dialogInvite(this, this.layoutInflater, (notification.getTypedNotification() as NotificationInviteObject), { setUnreadMessage() })
                }
                NotificationObject.Type.chat_message.name -> {
                    setUnreadMessage(true)
                    notification.markAsRead()
                }
                else -> notification.markAsRead()
            }
            Log.d("FirebaseNotification", "Notification N°${notification.id}")
        }
    }
    private fun updateChatUser() {
        val paginationObject = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        ConnectionService.instance.getAll<UserObject>(paginationObject).doOnSuccess  { chatUsers ->
            if (chatUsers.second.component2() == null && !chatUsers.second.component1().isNullOrEmpty()) {
                ChatService.instance.getAll<ChatObject>(paginationObject).doOnSuccess { conversations ->
                    val chatUser : UserObject?
                    if (conversations.second.component2() == null && !conversations.second.component1().isNullOrEmpty()) {
                        val lastConversationUser = conversations.second.component1()?.firstOrNull()?.with
                        chatUser = chatUsers.second.component1()?.find { it.uid == lastConversationUser }
                    } else {
                        chatUser = chatUsers.second.component1()?.firstOrNull()
                    }
                    UserManager.instance.saveChatUser(this, chatUser ?: UserObject())
                    setUnreadMessage()
                }.subscribe()
            }
        }.subscribe()
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
        currentFragment = supportFragmentManager.findFragmentById(R.id.navigationFragment)
        nfcReader!!.onResume()
    }

    override fun onPause() {
        currentFragment = null
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

    override fun onDestroy() {
        currentFragment = null
        super.onDestroy()
    }

}
