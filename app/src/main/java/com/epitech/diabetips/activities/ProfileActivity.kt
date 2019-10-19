package com.epitech.diabetips.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.emailInput

class ProfileActivity : AppCompatActivity() {

    private var isChangingEmail: Boolean = false
    private var isChangingPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        darkModeButton.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            ModeManager.instance.saveDarkMode(this, AppCompatDelegate.getDefaultNightMode())
            val intent = intent
            finish()
            startActivity(intent)
        }
        emailButton.setOnClickListener {
            emailButtonClick()
        }
        emailInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                emailButtonClick()
                true
            } else {
                false
            }
        }
        newPasswordButton.setOnClickListener {
            changePasswordDisplay()
        }
        getAccountInfo()
    }

    private fun getAccountInfo() {
        val account = AccountManager.instance.getAccount(this)
        firstNameText.text = account.first_name
        nameText.text = account.last_name
        emailText.text = account.email
        emailInput.setText(account.email)
    }

    private fun emailButtonClick() {
        if (isChangingEmail) {
            if (emailInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                Toast.makeText(this, getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show()
                return
            }
            emailText.text = emailInput.text
            val account : AccountObject = AccountManager.instance.getAccount(this)
            account.email = emailInput.text.toString()
            UserService.instance.updateUser(account).doOnSuccess {
                if (it.second.component2() == null) {
                    AccountManager.instance.saveAccount(this, it.second.component1()!!)
                } else {
                    Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
        isChangingEmail = !isChangingEmail
        if (isChangingEmail) {
            emailText.visibility = View.INVISIBLE
            emailInput.visibility = View.VISIBLE
            emailButton.rightIconDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_check)
            emailInput.requestFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(emailInput, 0)
        } else {
            emailText.visibility = View.VISIBLE
            emailInput.visibility = View.INVISIBLE
            emailButton.rightIconDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_edit)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(emailInput.windowToken, 0)
        }
    }

    private fun changePasswordDisplay() {
        if (isChangingPassword) {
            if (passwordInput.text.toString().length < resources.getInteger(R.integer.password_length)) {
                Toast.makeText(this, getString(R.string.password_length_error), Toast.LENGTH_SHORT).show()
            }
            else if (newPasswordInput.text.toString().isNotEmpty() &&
                newPasswordInput.text.toString() == newPasswordConfirmInput.text.toString()) {
                Toast.makeText(this, getString(R.string.password_match_error), Toast.LENGTH_SHORT).show()
            } else {
                val account : AccountObject = AccountManager.instance.getAccount(this)
                account.password = newPasswordInput.text.toString()
                UserService.instance.updateUser(account).doOnSuccess {
                    if (it.second.component2() == null) {
                        AccountManager.instance.saveAccount(this, it.second.component1()!!)
                    } else {
                        Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
                newPasswordConfirmInput.visibility = View.INVISIBLE
                newPasswordInput.visibility = View.INVISIBLE
                newPasswordButton.rightIconDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_edit)
                isChangingPassword = false
            }
        } else {
            newPasswordConfirmInput.visibility = View.VISIBLE
            newPasswordInput.visibility = View.VISIBLE
            newPasswordButton.rightIconDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_check)
            isChangingPassword = true
        }
    }
}
