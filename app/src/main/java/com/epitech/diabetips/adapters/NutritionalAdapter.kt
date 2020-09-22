package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.NutritionalItemViewHolder
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.NutritionalObject

class NutritionalAdapter(private val nutritions: ArrayList<NutritionalObject> = arrayListOf(),
                         private var quantity: Float = 100f)
    : RecyclerView.Adapter<NutritionalItemViewHolder>() {

    init {
        sortNutritions()
    }

    private val selectedIngredients: ArrayList<IngredientObject> = arrayListOf()

    fun setNutritions(nutritionList: ArrayList<NutritionalObject>, newQuantity: Float = quantity) {
        quantity = newQuantity
        nutritions.clear()
        nutritions.addAll(nutritionList)
        sortNutritions()
        notifyDataSetChanged()
    }

    private fun sortNutritions() {
        nutritions.sortBy { nutritionalObject -> nutritionalObject.type }
    }

    override fun getItemCount(): Int = nutritions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionalItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NutritionalItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: NutritionalItemViewHolder, position: Int) {
        holder.bind(nutritions[position], quantity)
    }

}

