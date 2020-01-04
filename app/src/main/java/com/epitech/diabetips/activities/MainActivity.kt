package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.emailInput
import kotlinx.android.synthetic.main.activity_main.emailInputLayout
import kotlinx.android.synthetic.main.activity_main.passwordInput
import kotlinx.android.synthetic.main.activity_main.passwordInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(ModeManager.instance.getDarkMode(this))
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FuelManager.instance.basePath = getString(R.string.api_base_url)
        FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json; charset=utf-8")
        loginButton.setOnClickListener {
            if (validateFields()) {
                var account = getAccountFromFields() //TODO : change to val when using real connection with the API
                //UserService.instance.login(account).subscribe() //TODO : uncomment when using real connection with the API

                //TODO : Remove when using real connection with the API
                var login = false
                UserService.instance.getAllUsers().doOnSuccess {
                    if (it.second.component2() == null) {
                        for (user in it.second.component1()!!) {
                            if (user.email == account.email) {
                                account = user
                                login = true
                                AccountManager.instance.saveAccount(this, account)
                                startActivity(Intent(this, NavigationActivity::class.java))
                            }
                        }
                        if (!login) {
                            emailInputLayout.error = getString(R.string.login_invalid)
                            passwordInputLayout.error = getString(R.string.login_invalid)
                        }
                    }
                }.subscribe()

                //TODO : Uncomment when using real connection with the API
                //AccountManager.instance.saveAccount(this, account)
                //startActivity(Intent(this, HomeActivity::class.java))
            }
        }
        signUpLinkButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateFields() : Boolean {
        var error = false
        if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
            emailInputLayout.error = getString(R.string.email_invalid_error)
            error = true
        } else {
            emailInputLayout.error = null
        }
        if (passwordInput.text.toString().isEmpty()) {
            passwordInputLayout.error = getString(R.string.password_incorrect_error)
            error = true
        } else {
            passwordInputLayout.error = null
        }
        return !error
    }

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        return account
    }
}
