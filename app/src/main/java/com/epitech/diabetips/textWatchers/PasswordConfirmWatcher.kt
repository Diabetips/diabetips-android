package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.Editable
import android.widget.EditText
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

class PasswordConfirmWatcher(context: Context?,
                             passwordConfirmLayout: TextInputLayout,
                             private val passwordInput: EditText) : InputWatcher(context, passwordConfirmLayout) {

    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != passwordInput.text.toString()) {
            inputLayout.error = context?.getString(R.string.password_match_error)
        } else {
            super.afterTextChanged(s)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}
