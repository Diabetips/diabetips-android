package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.HttpURLConnection.HTTP_CONFLICT

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpButton.setOnClickListener {
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
                UserService.instance.registerUser(account).doOnSuccess {
                    if (it.second.component2() == null) {
                        AccountManager.instance.saveAccount(this, it.second.component1()!!)
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

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        account.first_name = firstNameInput.text.toString()
        account.last_name = nameInput.text.toString()
        return account
    }
}
