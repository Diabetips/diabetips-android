package com.epitech.diabetips.utils

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout

class MaterialHandler {

    private object Holder { val INSTANCE = MaterialHandler() }

    companion object {
        val instance: MaterialHandler by lazy { Holder.INSTANCE }
    }

    fun handleTextInputLayoutSize(vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child: View = vg.getChildAt(i)
            if (child is TextInputLayout) {
                handleCheckableImageButtonSize(child)
            } else if (child is ViewGroup) {
                handleTextInputLayoutSize(child)
            }
        }
    }

    private fun handleCheckableImageButtonSize(vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child: View = vg.getChildAt(i)
            if (child is CheckableImageButton) {
                child.minimumHeight = 0
            } else if (child is AppCompatTextView) {
                child.layoutParams.apply {
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                child.gravity = Gravity.CENTER
            } else if (child is ViewGroup) {
                handleCheckableImageButtonSize(child)
            }
        }
    }
}