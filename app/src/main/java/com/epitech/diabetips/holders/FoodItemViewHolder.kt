package com.epitech.diabetips.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.utils.ImageHandler
import kotlinx.android.synthetic.main.item_food.view.*

class FoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_food, parent, false)) {
    private var foodText: TextView? = null
    private var sugar100gText: TextView? = null
    private var unitText: TextView? = null
    private var foodImage: ImageView? = null
    private var foodCkeckBox: CheckBox? = null
    private var context: Context

    init {
        foodText = itemView.foodText
        sugar100gText = itemView.foodSugar100g
        unitText = itemView.foodUnitText
        foodImage = itemView.foodImage
        foodCkeckBox = itemView.foodCheckBox
        context = inflater.context
    }

    fun getFoodCheckBox() : CheckBox? {
        return foodCkeckBox
    }

    fun bind(food: FoodObject, selected: Boolean = false, onItemClickListener : ((FoodObject, CheckBox?) -> Unit)? = null) {
        foodText?.text = food.name
        sugar100gText?.text = "${food.sugars_100g}${context.getString(R.string.unit_g)}"
        unitText?.text = food.unit
        foodCkeckBox?.isChecked = selected
        itemView.setOnClickListener {
            foodCkeckBox?.isChecked = true
            onItemClickListener?.invoke(food, foodCkeckBox)
        }
        if (foodImage != null) {
            ImageHandler.instance.loadImage(foodImage!!, context, FoodService.instance.getPictureUrl(food.id), R.drawable.ic_food)
        }
    }
}

