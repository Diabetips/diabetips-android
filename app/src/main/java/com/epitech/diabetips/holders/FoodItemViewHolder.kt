package com.epitech.diabetips.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.epitech.diabetips.R
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.storages.FoodObject
import com.epitech.diabetips.utils.ImageHandler
import kotlinx.android.synthetic.main.item_food.view.*

class FoodItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_food, parent, false)) {
    private var foodText: TextView? = null
    private var unitText: TextView? = null
    private var foodImage: ImageView? = null
    private var context: Context

    init {
        foodText = itemView.foodText
        unitText = itemView.foodUnitText
        foodImage = itemView.foodImage
        context = inflater.context
    }

    fun bind(food: FoodObject, onItemClickListener : ((FoodObject) -> Unit)? = null) {
        foodText?.text = food.name
        unitText?.text = food.unit
        itemView.setOnClickListener {onItemClickListener?.invoke(food)}
        if (foodImage != null)
            ImageHandler.instance.loadImage(foodImage!!, context, FoodService.instance.getFoodPictureUrl(food.id), R.drawable.ic_food)
    }
}

