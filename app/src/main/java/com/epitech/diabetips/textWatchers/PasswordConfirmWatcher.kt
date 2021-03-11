package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.Editable
import android.widget.EditText
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

class PasswordConfirmWatcher(context: Context?,
                             passwordConfirmLayout: TextInputLayout,
                             private val passwordInput: EditText) : InputIconWatcher(context, passwordConfirmLayout) {

    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != passwordInput.text.toString()) {
            setError(R.string.password_match_error)
        } else {
            super.afterTextChanged(s)
        }
    }
}
