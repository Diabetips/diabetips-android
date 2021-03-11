package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.Editable
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

class PasswordWatcher(context: Context?,
                      passwordLayout: TextInputLayout,
                      private val complexityCheck: Boolean = false,
                      private val clearErrorOnEmpty: Boolean = false) : InputIconWatcher(context, passwordLayout) {

    override fun afterTextChanged(s: Editable?) {
        if (complexityCheck && (!clearErrorOnEmpty || s.toString().isNotEmpty())) {
             if (!s.toString().matches(Regex("^.*[a-z].*$"))) {
                 setError(R.string.password_lowercase_error)
            } else if (!s.toString().matches(Regex("^.*[A-Z].*$"))) {
                 setError(R.string.password_uppercase_error)
            } else if (!s.toString().matches(Regex("^.*[0-9].*$"))) {
                 setError(R.string.password_digit_error)
            } else if (context != null && s.toString().length < context.resources.getInteger(R.integer.password_length)) {
                setError(R.string.password_length_error)
            } else {
                super.afterTextChanged(s)
            }
        } else {
            super.afterTextChanged(s)
        }
    }
}
