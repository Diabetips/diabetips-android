package com.epitech.diabetips.textWatchers

import android.content.Context
import android.text.TextWatcher
import android.text.Editable
import com.epitech.diabetips.R
import com.google.android.material.textfield.TextInputLayout

open class NumberWatcher(private val context: Context?,
                         private val inputLayout: TextInputLayout,
                         private val errorId: Int = R.string.empty_field_error,
                         private val min: Float? = null,
                         private val max: Float? = null) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        val value: Float? = s.toString().toFloatOrNull()
        if (value == null || (min != null && min > value) || (max != null && max < value)) {
            inputLayout.error = context?.getString(errorId)
        } else {
            inputLayout.error = null
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}
