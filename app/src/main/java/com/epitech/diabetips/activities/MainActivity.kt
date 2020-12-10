package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.services.BiometricService
import com.epitech.diabetips.services.TokenService
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.BiometricObject
import com.epitech.diabetips.storages.NotificationObject
import com.epitech.diabetips.storages.UserObject
import com.epitech.diabetips.textWatchers.EmailWatcher
import com.epitech.diabetips.textWatchers.PasswordWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.DialogHandler
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_password_forgot.view.*
import org.json.JSONObject
import java.lang.Exception
import java.nio.charset.Charset

class MainActivity : ADiabetipsActivity(R.layout.activity_main) {

    private var notification: NotificationObject = NotificationObject()
    private var passwordWatcher: PasswordWatcher? = null

    companion object {
        var running = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json; charset=utf-8")
        passwordWatcher = PasswordWatcher(this, passwordInputLayout)
        passwordInput.addTextChangedListener(passwordWatcher)
        emailInput.addTextChangedListener(EmailWatcher(this, emailInputLayout))
        loginButton.setOnClickListener {
            login()
        }
        signUpLinkButton.setOnClickListener {
            if (!mainSwipeRefresh.isRefreshing) {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
        forgotPasswordButton.setOnClickListener {
            forgotPassword()
        }
        if (AuthManager.instance.hasRefreshToken(this)) {
            changeSwipeLayoutState(true)
            TokenService.instance.refreshToken(this).doAfterSuccess {
                if (it.second.component2() == null) {
                    launchHomeActivity()
                } else {
                    changeSwipeLayoutState(false)
                }
            }.subscribe()
        }
        getParams()
        running = true
    }

    private fun getParams() {
        if (intent.hasExtra(getString(R.string.param_notification))) {
            notification = (intent.getSerializableExtra(getString(R.string.param_notification)) as NotificationObject)
        }
    }

    private fun login() {
        if (validateFields() && !mainSwipeRefresh.isRefreshing) {
            changeSwipeLayoutState(true)
            val account = getAccountFromFields()
            TokenService.instance.getToken(this, account.email, account.password).doAfterSuccess {
                if (it.second.component2() == null) {
                    UserManager.instance.removePreferences(this)
                    launchHomeActivity()
                } else {
                    try {
                        var error = it.first.data.toString(Charset.defaultCharset())
                        if (error.isNotBlank()) {
                            error = JSONObject(error).getString("error")
                            if (error == "registration_incomplete") {
                                emailInputLayout.error = getString(R.string.registration_incomplete)
                            } else {
                                emailInputLayout.error = getString(R.string.login_invalid)
                                passwordWatcher?.setError(R.string.login_invalid)
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.connexion_error), Toast.LENGTH_SHORT).show()
                    }
                    changeSwipeLayoutState(false)
                }
            }.subscribe()
        }
    }

    private fun launchHomeActivity(ignoreEmptyUser: Boolean = false) {
        val account = UserManager.instance.getUser(this)
        if (ignoreEmptyUser || account.uid != "") {
            if (notification.id.isNotEmpty()) {
                startActivity(Intent(this, NavigationActivity::class.java)
                    .putExtra(getString(R.string.param_notification), notification))
            } else {
                startActivity(Intent(this, NavigationActivity::class.java))
            }
            changeSwipeLayoutState(false)
        } else {
            UserService.instance.get<UserObject>("me").doOnSuccess { userResponse ->
                if (userResponse.second.component2() == null) {
                    UserManager.instance.saveUser(this, userResponse.second.component1()!!)
                    BiometricService.instance.get<BiometricObject>().doOnSuccess { biometricResponse ->
                        if (biometricResponse.second.component2() == null) {
                            UserManager.instance.saveBiometric(this, biometricResponse.second.component1()!!)
                        }
                        launchHomeActivity()
                    }.subscribe()
                } else {
                    launchHomeActivity(true)
                }
            }.subscribe()
        }
    }

    private fun forgotPassword() {
        if (!mainSwipeRefresh.isRefreshing) {
            DialogHandler.createDialog(this, layoutInflater, R.layout.dialog_password_forgot) { view, dialog ->
                view.emailResetPasswordInput.setText(emailInput.text.toString())
                view.resetPasswordButton.setOnClickListener {
                    if (view.emailResetPasswordInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(view.emailResetPasswordInput.text.toString()).matches()) {
                        view.emailResetPasswordInputLayout.error = getString(R.string.email_invalid_error)
                    } else {
                        view.emailResetPasswordInputLayout.error = null
                        TokenService.instance.resetPassword(this, view.emailResetPasswordInput.text.toString()).doAfterSuccess {
                            if (it.second.component2() == null) {
                                Toast.makeText(this, getString(R.string.reset_password), Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }.subscribe()
                    }
                }
            }
        }
    }

    private fun validateFields() : Boolean {
        emailInput.text = emailInput.text
        passwordInput.text = passwordInput.text
        return emailInputLayout.error == null
                && passwordInputLayout.error == null
    }

    private fun getAccountFromFields() : UserObject {
        val account = UserObject()
        account.email = emailInput.text.toString()
        account.password = passwordInput.text.toString()
        return account
    }

    private fun changeSwipeLayoutState(state: Boolean) {
        mainSwipeRefresh.isRefreshing = state
        mainSwipeRefresh.isEnabled = state
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}
