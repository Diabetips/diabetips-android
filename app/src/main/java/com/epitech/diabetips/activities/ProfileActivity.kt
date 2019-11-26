package com.epitech.diabetips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.managers.ModeManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
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
        updateProfileButton.setOnClickListener {
            updateProfile()
        }
        getAccountInfo()
    }

    private fun getAccountInfo() {
        val account = AccountManager.instance.getAccount(this)
        firstNameProfileInput.setText(account.first_name)
        nameProfileInput.setText(account.last_name)
        emailProfileInput.setText(account.email)
    }

    private fun updateProfile() {
        val account : AccountObject = AccountManager.instance.getAccount(this)
        if (nameProfileInput.text.toString().isEmpty() || firstNameProfileInput.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.name_invalid_error), Toast.LENGTH_SHORT).show()
            return
        }
        if (emailProfileInput.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailProfileInput.text.toString()).matches()) {
            Toast.makeText(this, getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show()
            return
        }
        if (newPasswordInput.text.toString().isNotEmpty()) {
            if (newPasswordInput.text.toString().length < resources.getInteger(R.integer.password_length)) {
                Toast.makeText(this, getString(R.string.password_length_error), Toast.LENGTH_SHORT).show()
                return
            } else if (newPasswordInput.text.toString() != newPasswordConfirmInput.text.toString()) {
                Toast.makeText(this, getString(R.string.password_match_error), Toast.LENGTH_SHORT).show()
                return
            }
            account.password = newPasswordInput.text.toString()
        }
        account.last_name = nameProfileInput.text.toString()
        account.first_name = firstNameProfileInput.text.toString()
        account.email = emailProfileInput.text.toString()
        UserService.instance.updateUser(account).doOnSuccess {
        if (it.second.component2() == null) {
            AccountManager.instance.saveAccount(this, it.second.component1()!!)
            Toast.makeText(this, getString(R.string.update_profile), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
        }
        }.subscribe()
    }
}
