package com.epitech.diabetips

import com.epitech.diabetips.Services.DiabetipsService
import com.epitech.diabetips.Storages.AccountObject
import org.junit.Test

import org.junit.Assert.*

/**
 * Local unit test, which will execute on the development machine.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {

    private val expectedAccount = AccountObject("nom.prenom@test.com", "password","NOM", "Prénom")

    @Test
    fun login_isCorrect() {
        DiabetipsService.instance.login(expectedAccount).doOnSuccess { res ->
            assertEquals(expectedAccount, res.second.component2())
        }
    }

    @Test
    fun signUp_isCorrect() {
        DiabetipsService.instance.signUp(expectedAccount).doOnSuccess { res ->
            assertEquals(expectedAccount, res.second.component2())
        }
    }

    @Test
    fun getAccount_isCorrect() {
        DiabetipsService.instance.getAccount().doOnSuccess { res ->
            assertEquals(expectedAccount, res.second.component2())
        }
    }
}
