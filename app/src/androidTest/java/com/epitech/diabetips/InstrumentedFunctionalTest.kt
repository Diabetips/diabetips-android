package com.epitech.diabetips

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.epitech.diabetips.Managers.AccountManager
import com.epitech.diabetips.Managers.AuthManager
import com.epitech.diabetips.Storages.AccountObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedFunctionalTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun saveAndGetAllManager() {
        //Expected values
        val expectedAccessToken = "49fad390491a5b547d0f782309b6a5b33f7ac087"
        val expectedRefreshToken = "8xLOxBtZp8"
        val expectedAccount = AccountObject("nom.prenom@email.com", "password", "NOM", "Prénom")
        //Save values with managers
        AuthManager.instance.saveAccessToken(instrumentationContext, expectedAccessToken)
        AuthManager.instance.saveRefreshToken(instrumentationContext, expectedRefreshToken)
        AccountManager.instance.saveObject(instrumentationContext, expectedAccount)
        //Get values with managers
        val actualAccessToken = AuthManager.instance.getAccessToken(instrumentationContext)
        val actualRefreshToken = AuthManager.instance.getRefreshToken(instrumentationContext)
        val actualAccount =  AccountManager.instance.getAccount(instrumentationContext)
        //Asserts
        assertEquals("Wrong access token.", expectedAccessToken, actualAccessToken)
        assertEquals("Wrong refresh token.", expectedRefreshToken, actualRefreshToken)
        assertEquals("Wrong account.", expectedAccount, actualAccount)
    }
}
