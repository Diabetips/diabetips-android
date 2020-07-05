package com.epitech.diabetips.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AVisibilityAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    private var visibleOnEmpty : View? = null
    private var visibleOnNonempty : View? = null

    fun setVisibilityElements(newVisibleOnEmpty: View? = null, newVisibleOnNonempty: View? = null, updateVisibility: Boolean = true) {
        visibleOnEmpty = newVisibleOnEmpty
        visibleOnNonempty = newVisibleOnNonempty
        if (updateVisibility) {
            updateVisibility()
        }
    }

    protected fun updateVisibility() {
        visibleOnEmpty?.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
        visibleOnNonempty?.visibility = if (itemCount > 0) View.VISIBLE else View.GONE
    }
}

