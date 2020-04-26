package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.services.RecipeService
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.utils.ImageHandler
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe, parent, false)) {
    private var recipeText: TextView? = null
    private var recipeDescription: TextView? = null
    private var recipeImage: ImageView? = null
    private var context: Context

    init {
        recipeText = itemView.recipeText
        recipeDescription = itemView.recipeDescription
        recipeImage = itemView.recipeImage
        context = inflater.context
    }

    fun bind(recipe: RecipeObject, onItemClickListener : ((RecipeObject) -> Unit)? = null) {
        recipeText?.text = recipe.name
        recipeDescription?.text = recipe.description
        itemView.setOnClickListener {onItemClickListener?.invoke(recipe)}
        if (recipeImage != null) {
            ImageHandler.instance.loadImage(recipeImage!!, context, RecipeService.instance.getRecipePictureUrl(recipe.id), R.drawable.ic_unknown, false)
        }
    }
}

