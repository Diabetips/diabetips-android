package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout

class MaterialHandler {

    companion object {

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

        fun getColorFromAttribute(context: Context, attributeId: Int) : Int {
            val attribute = intArrayOf(attributeId)
            val array = context.theme.obtainStyledAttributes(attribute)
            val color = array.getColor(0, ContextCompat.getColor(context, R.color.colorTextDark))
            array.recycle()
            return color
        }
    }
}