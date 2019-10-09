package com.epitech.diabetips.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.Storages.RecipeObject

class RecipeAdapter(private val recipes: ArrayList<RecipeObject> = arrayListOf(),
                  private val onItemClickListener : ((RecipeObject) -> Unit)? = null)
    : RecyclerView.Adapter<RecipeItemViewHolder>() {

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeItemViewHolder, position: Int) {
        holder.bind(recipes[position], onItemClickListener)
    }

}
