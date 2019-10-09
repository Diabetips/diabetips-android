package com.epitech.diabetips.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.Managers.AccountManager
import com.epitech.diabetips.Managers.ModeManager
import com.epitech.diabetips.R
import com.epitech.diabetips.Services.DiabetipsService
import com.epitech.diabetips.Storages.AccountObject
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.HttpException
import kotlinx.android.synthetic.main.activity_main.*
import java.net.HttpURLConnection.HTTP_CONFLICT

class MainActivity : AppCompatActivity() {

    private var signUp: Boolean = false

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
            if (signUp) {
                changeSignUpVisibility(View.INVISIBLE)
                signUp = false
            } else {
                if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                    Toast.makeText(this, getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show()
                } else if (passwordInput.text.toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.password_incorrect_error), Toast.LENGTH_SHORT).show()
                } else {
                    var account = getAccountFromFields() //TODO : change to val when using real connection with the API
                    //DiabetipsService.instance.login(account).subscribe() //TODO : uncomment when using real connection with the API

                    //TODO : Remove when using real connection with the API
                    var login = false
                    DiabetipsService.instance.getAllUsers().doOnSuccess {
                        if (it.second.component2() == null) {
                            for (user in it.second.component1()!!) {
                                if (user.email == account.email) {
                                    login = true
                                    AccountManager.instance.saveObject(this, account)
                                    startActivity(Intent(this, HomeActivity::class.java))
                                }
                            }
                            if (!login) {
                                Toast.makeText(this, getString(R.string.login_invalid), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.subscribe()

                    //TODO : Uncomment when using real connection with the API
                    //AccountManager.instance.saveObject(this, account)
                    //startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }
        signUpButton.setOnClickListener {
            if (!signUp) {
                changeSignUpVisibility(View.VISIBLE)
                signUp = true
            } else {
                if (nameInput.text.toString().isEmpty() || firstNameInput.text.toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.name_invalid_error), Toast.LENGTH_SHORT).show()
                } else if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                    Toast.makeText(this, getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show()
                } else if (passwordInput.text.toString().length < resources.getInteger(R.integer.password_length)) {
                    Toast.makeText(this, getString(R.string.password_length_error), Toast.LENGTH_SHORT).show()
                } else if (passwordInput.text.toString().isEmpty() ||
                    passwordInput.text.toString() != passwordConfirmInput.text.toString()) {
                    Toast.makeText(this, getString(R.string.password_match_error), Toast.LENGTH_SHORT).show()
                } else {
                    val account = getAccountFromFields()
                    DiabetipsService.instance.registerUser(account).doOnSuccess {
                        if (it.second.component2() == null) {
                            AccountManager.instance.saveObject(this, it.second.component1()!!)
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else if (it.second.component2()!!.response.statusCode == HTTP_CONFLICT) {
                            Toast.makeText(this, getString(R.string.email_already_taken), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                }
            }
        }
        themeButton.setOnClickListener {
            startActivity(Intent(this, StyleActivity::class.java))
        }
    }

    private fun changeSignUpVisibility(visibility: Int) {
        nameInput.visibility = visibility
        firstNameInput.visibility = visibility
        passwordConfirmInput.visibility = visibility
    }

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        if (signUp) {
            account.first_name = firstNameInput.text.toString()
            account.last_name = nameInput.text.toString()
        }
        return account
    }
}
