package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.Editable
import android.util.Patterns
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

class EmailWatcher(context: Context?,
                  emailLayout: TextInputLayout) : InputWatcher(context, emailLayout, true, R.string.email_invalid_error) {

    override fun afterTextChanged(s: Editable?) {
        if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            setError(R.string.email_invalid_error)
        } else {
            super.afterTextChanged(s)
        }
    }
}
