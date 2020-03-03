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
            inputLayout.error = context?.getString(R.string.email_invalid_error)
        } else {
            super.afterTextChanged(s)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}
