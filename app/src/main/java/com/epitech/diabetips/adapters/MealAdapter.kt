package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.storages.MealObject

class MealAdapter(private val meals: ArrayList<MealObject> = arrayListOf(),
                  private val onItemClickListener : ((MealObject) -> Unit)? = null) {
}
