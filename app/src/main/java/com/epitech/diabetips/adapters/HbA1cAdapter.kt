package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.epitech.diabetips.holders.HbA1cItemViewHolder
import com.epitech.diabetips.holders.RecipeItemViewHolder
import com.epitech.diabetips.storages.HbA1cObject
import com.epitech.diabetips.storages.RecipeObject

class HbA1cAdapter(private val hbA1c: ArrayList<HbA1cObject> = arrayListOf(),
                   private val onItemClickListener : ((HbA1cObject) -> Unit)? = null)
    : AVisibilityAdapter<HbA1cItemViewHolder>() {

    fun setHbA1c(hbA1cList: Array<HbA1cObject>) {
        hbA1c.clear()
        hbA1c.addAll(hbA1cList)
        notifyDataSetChanged()
        updateVisibility()
    }

    fun addHbA1c(hbA1cList: Array<HbA1cObject>) {
        hbA1c.addAll(hbA1cList)
        notifyItemRangeInserted(hbA1c.size - hbA1cList.size, hbA1cList.size)
        updateVisibility()
    }

    override fun getItemCount(): Int = hbA1c.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HbA1cItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HbA1cItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HbA1cItemViewHolder, position: Int) {
        holder.bind(hbA1c[position], onItemClickListener)
    }

}
