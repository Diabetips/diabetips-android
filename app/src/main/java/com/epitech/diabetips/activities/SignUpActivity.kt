package com.epitech.diabetips.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.textWatchers.EmailWatcher
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.textWatchers.PasswordConfirmWatcher
import com.epitech.diabetips.textWatchers.PasswordWatcher
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
        firstNameInput.addTextChangedListener(InputWatcher(this, firstNameInputLayout, true, R.string.first_name_invalid_error))
        nameInput.addTextChangedListener(InputWatcher(this, nameInputLayout, true, R.string.name_invalid_error))
        emailInput.addTextChangedListener(EmailWatcher(this, emailInputLayout))
        passwordInput.addTextChangedListener(PasswordWatcher(this, passwordInputLayout, true))
        passwordConfirmInput.addTextChangedListener(PasswordConfirmWatcher(this, passwordConfirmInputLayout, passwordInput))
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
        backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun validateFields() : Boolean {
        nameInput.text = nameInput.text
        firstNameInput.text = firstNameInput.text
        emailInput.text = emailInput.text
        passwordInput.text = passwordInput.text
        passwordConfirmInput.text = passwordConfirmInput.text
        return nameInputLayout.error == null
                && firstNameInputLayout.error == null
                && emailInputLayout.error == null
                && passwordInputLayout.error == null
                && passwordConfirmInputLayout.error == null
    }

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        account.first_name = firstNameInput.text.toString()
        account.last_name = nameInput.text.toString()
        account.lang = getString(R.string.lang)
        return account
    }
}
