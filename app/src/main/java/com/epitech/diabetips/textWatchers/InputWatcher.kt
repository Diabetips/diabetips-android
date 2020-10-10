package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.TextWatcher
import android.text.Editable
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

open class InputWatcher(protected val context: Context?,
                        protected val inputLayout: TextInputLayout,
                        private val emptyCheck: Boolean = false,
                        private val emptyErrorId: Int = R.string.empty_field_error) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        if (emptyCheck && s.toString().isBlank()) {
            setError(emptyErrorId)
        } else {
            setError(null)
        }
    }

    open fun setError(errorId: Int?) {
        inputLayout.error = if (errorId != null) context?.getString(errorId) else null
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}
