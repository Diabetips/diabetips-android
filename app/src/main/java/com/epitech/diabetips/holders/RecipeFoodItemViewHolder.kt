package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.utils.ImageHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_recipe_food.view.*

class RecipeFoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_recipe_food, parent, false)) {
    private val context: Context = inflater.context
    private var recipeFoodText: TextView? = null
    private var recipeFoodQuantity: TextView? = null
    private var recipeFoodButton: ImageButton? = null
    private var recipeFoodImage: CircleImageView? = null

    init {
        recipeFoodText = itemView.recipeFoodText
        recipeFoodQuantity = itemView.recipeFoodQuantity
        recipeFoodButton = itemView.recipeFoodButton
        recipeFoodImage = itemView.recipeFoodImage
    }

    fun getRecipeFoodButton() : ImageButton? {
        return recipeFoodButton
    }

    fun bind(food: IngredientObject, onItemClickListener : ((IngredientObject, TextView?) -> Unit)? = null) {
        recipeFoodText?.text = food.food.name
        recipeFoodQuantity?.text = "${food.quantity} ${food.food.unit}"
        itemView.setOnClickListener {onItemClickListener?.invoke(food, recipeFoodQuantity)}
        if (recipeFoodImage != null) {
            ImageHandler.instance.loadImage(recipeFoodImage!!, context, FoodService.instance.getFoodPictureUrl(food.food.id), R.drawable.ic_food)
        }
    }
}

