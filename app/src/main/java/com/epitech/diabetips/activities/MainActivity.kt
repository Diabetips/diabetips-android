package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*

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
            if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                Toast.makeText(this, getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show()
            } else if (passwordInput.text.toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.password_incorrect_error), Toast.LENGTH_SHORT).show()
            } else {
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
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                        }
                        if (!login) {
                            Toast.makeText(this, getString(R.string.login_invalid), Toast.LENGTH_SHORT).show()
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
        themeButton.setOnClickListener {
            startActivity(Intent(this, StyleActivity::class.java))
        }
    }

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        return account
    }
}
