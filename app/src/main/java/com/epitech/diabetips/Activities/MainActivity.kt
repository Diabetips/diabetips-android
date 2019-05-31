package com.epitech.diabetips.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.epitech.diabetips.R
import com.epitech.diabetips.Services.DiabetipsService
import com.epitech.diabetips.Storages.AccountObject
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var signUp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FuelManager.instance.basePath = getString(R.string.api_base_url)
        loginButton.setOnClickListener {
            if (signUp) {
                changeSignUpVisibility(View.INVISIBLE)
                signUp = false
            } else {
                DiabetipsService.instance.login(AccountObject()).subscribe()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
        signUpButton.setOnClickListener {
            if (!signUp) {
                changeSignUpVisibility(View.VISIBLE)
                signUp = true
            } else {
                if (passwordInput.text.toString().isNotEmpty() &&
                    passwordInput.text.toString() == passwordConfirmInput.text.toString()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, getString(R.string.password_match_error), Toast.LENGTH_SHORT).show()
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
}
