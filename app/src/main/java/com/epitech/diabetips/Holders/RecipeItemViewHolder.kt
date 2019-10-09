package com.epitech.diabetips.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.Storages.RecipeObject
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe, parent, false)) {
    private var recipeText: TextView? = null
    private var recipeDescription: TextView? = null

    init {
        recipeText = itemView.recipeText
        recipeDescription = itemView.recipeDescription
    }

    fun bind(recipe: RecipeObject, onItemClickListener : ((RecipeObject) -> Unit)? = null) {
        recipeText?.text = recipe.name
        recipeDescription?.text = recipe.description
        itemView.setOnClickListener {onItemClickListener?.invoke(recipe)}
    }
}

