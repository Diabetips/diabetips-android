package com.epitech.diabetips.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.services.TokenService
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.textWatchers.EmailWatcher
import com.epitech.diabetips.textWatchers.PasswordWatcher
import com.epitech.diabetips.utils.MaterialHandler
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_password_forgot.view.*
import org.json.JSONObject
import java.nio.charset.Charset

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
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        FuelManager.instance.basePath = getString(R.string.api_base_url)
        FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json; charset=utf-8")
        emailInput.addTextChangedListener(EmailWatcher(this, emailInputLayout))
        passwordInput.addTextChangedListener(PasswordWatcher(this, passwordInputLayout))
        loginButton.setOnClickListener {
            if (validateFields() && !mainSwipeRefresh.isRefreshing) {
                changeSwipeLayoutState(true)
                val account = getAccountFromFields()
                TokenService.instance.getToken(this, account.email, account.password).doAfterSuccess {
                    if (it.second.component2() == null) {
                        startActivity(Intent(this, NavigationActivity::class.java))
                    } else {
                        val error = JSONObject(it.first.data.toString(Charset.defaultCharset())).getString("error")
                        if (error == "registration_incomplete") {
                            emailInputLayout.error = getString(R.string.registration_incomplete)
                        } else {
                            emailInputLayout.error = getString(R.string.login_invalid)
                            passwordInputLayout.error = getString(R.string.login_invalid)
                        }
                    }
                    changeSwipeLayoutState(false)
                }.subscribe()
            }
        }
        signUpLinkButton.setOnClickListener {
            if (!mainSwipeRefresh.isRefreshing) {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
        forgotPasswordButton.setOnClickListener {
            if (!mainSwipeRefresh.isRefreshing) {
                val view = layoutInflater.inflate(R.layout.dialog_password_forgot, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this).setView(view).create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                view.emailResetPasswordInput.setText(emailInput.text.toString())
                view.resetPasswordButton.setOnClickListener {
                    if (view.emailResetPasswordInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(view.emailResetPasswordInput.text.toString()).matches()) {
                        view.emailResetPasswordInputLayout.error = getString(R.string.email_invalid_error)
                    } else {
                        view.emailResetPasswordInputLayout.error = null
                        TokenService.instance.resetPassword(view.emailResetPasswordInput.text.toString()).doOnSuccess {
                            if (it.second.component2() == null) {
                                Toast.makeText(this, getString(R.string.reset_password), Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }.subscribe()
                    }
                }
                dialog.show()
            }
        }
        if (AuthManager.instance.hasRefreshToken(this)) {
            changeSwipeLayoutState(true)
            TokenService.instance.refreshToken(this).doAfterSuccess {
                if (it.second.component2() == null) {
                    startActivity(Intent(this, NavigationActivity::class.java))
                }
                changeSwipeLayoutState(false)
            }.subscribe()
        }
    }

    private fun validateFields() : Boolean {
        emailInput.text = emailInput.text
        passwordInput.text = passwordInput.text
        return emailInputLayout.error == null
                && passwordInputLayout.error == null
    }

    private fun getAccountFromFields() : AccountObject {
        val account = AccountObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        return account
    }

    private fun changeSwipeLayoutState(state: Boolean) {
        mainSwipeRefresh.isRefreshing = state
        mainSwipeRefresh.isEnabled = state
    }
}