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
import com.epitech.diabetips.utils.MaterialHandler
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
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        signUpButton.setOnClickListener {
            if (validateFields()) {
                val account = getAccountFromFields()
                UserService.instance.registerUser(account).doOnSuccess {
                    if (it.second.component2() == null) {
                        Toast.makeText(this, getString(R.string.created_account), Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (it.second.component2()!!.response.statusCode == HTTP_CONFLICT) {
                        emailInputLayout.error = getString(R.string.email_already_taken)
                    } else {
                        Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            }
        }
    }

    private fun validateFields() : Boolean {
        var error = false
        if (nameInput.text.toString().isEmpty()) {
            nameInputLayout.error = getString(R.string.name_invalid_error)
            error = true
        } else {
            nameInputLayout.error = null
        }
        if (firstNameInput.text.toString().isEmpty()) {
            firstNameInputLayout.error = getString(R.string.first_name_invalid_error)
            error = true
        } else {
            firstNameInputLayout.error = null
        }
        if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
            emailInputLayout.error = getString(R.string.email_invalid_error)
            error = true
        } else {
            emailInputLayout.error = null
        }
        if (passwordInput.text.toString().length < resources.getInteger(R.integer.password_length)) {
            passwordInputLayout.error = getString(R.string.password_length_error)
            error = true
        } else {
            passwordInputLayout.error = null
        }
        if (passwordInput.text.toString().isNotEmpty() && passwordInput.text.toString() != passwordConfirmInput.text.toString()) {
            passwordConfirmInputLayout.error = getString(R.string.password_match_error)
            error = true
        } else {
            passwordConfirmInputLayout.error = null
        }
        return !error
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
