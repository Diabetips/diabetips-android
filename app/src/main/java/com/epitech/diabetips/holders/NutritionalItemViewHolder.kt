package com.epitech.diabetips.holders

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.NutritionalObject
import kotlinx.android.synthetic.main.item_nutritional.view.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class NutritionalItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_nutritional, parent, false)) {
    private var nutritionalText: TextView? = null
    private var nutritionalValueText: TextView? = null
    private var nutritionalProgressBar: ProgressBar? = null
    private var nutritionalValueLayout: LinearLayout? = null
    private var nutritionalQuantity: Float = 100f
    private var context: Context

    init {
        nutritionalText = itemView.nutritionalText
        nutritionalValueText = itemView.nutritionalValueText
        nutritionalProgressBar = itemView.nutritionalProgressBar
        nutritionalValueLayout = itemView.nutritionalValueLayout
        context = inflater.context
    }

    fun bind(nutrition: NutritionalObject, quantity: Float = 100f) {
        nutritionalQuantity = if (quantity == 0f) 1f else quantity
        when (nutrition.type) {
            NutritionalObject.NutritionalType.CALORIE -> changeNutritionalValue(R.string.nutritional_calorie, R.color.colorPurple, nutrition.value, R.string.unit_calorie, false, false)
            NutritionalObject.NutritionalType.CARBOHYDRATE -> changeNutritionalValue(R.string.nutritional_carbohydrate, R.color.colorAccent, nutrition.value, R.string.unit_g)
            NutritionalObject.NutritionalType.SUGAR -> changeNutritionalValue(R.string.nutritional_sugar, R.color.colorAccent, nutrition.value, R.string.unit_g, true)
            NutritionalObject.NutritionalType.FAT -> changeNutritionalValue(R.string.nutritional_fat, R.color.colorPrimary, nutrition.value, R.string.unit_g)
            NutritionalObject.NutritionalType.SATURATED_FAT -> changeNutritionalValue(R.string.nutritional_saturated_fat, R.color.colorPrimary, nutrition.value, R.string.unit_g, true)
            NutritionalObject.NutritionalType.FIBER -> changeNutritionalValue(R.string.nutritional_fiber, R.color.colorWarm, nutrition.value, R.string.unit_g)
            NutritionalObject.NutritionalType.PROTEIN -> changeNutritionalValue(R.string.nutritional_protein, R.color.colorGreen, nutrition.value, R.string.unit_g)
            NutritionalObject.NutritionalType.NUTRI_SCORE -> changeNutriScore(nutrition.value)
        }
    }

    private fun changeNutritionalValue(nameId: Int, colorId: Int, value: Float, unitId: Int, isChild: Boolean = false, displayPercent: Boolean = true) {
        val progress = (if (displayPercent) (value / nutritionalQuantity * 100f).toInt() else (nutritionalProgressBar?.max ?: 0))
        val text = "${value.roundToLong()} ${context.getString(unitId)}${if (displayPercent) " • ${progress}%" else ""}"
        changeNutritionalDisplay(nameId, colorId, text, progress, isChild)
    }

    private fun changeNutriScore(value: Float) {
        val text = when (value) {
            !in 'A'.toFloat()..'E'.toFloat() -> "?"
            value.roundToInt().toFloat() -> "${value.roundToInt().toChar()}"
            in Float.MIN_VALUE..value.roundToInt().toFloat() -> "${value.roundToInt().toChar()}+"
            else -> "${value.roundToInt().toChar()}-"
        }
        val colorId = when (text[0]) {
            'A' -> R.color.colorGreen
            'B' -> R.color.colorGreen
            'C' -> R.color.colorAccent
            'D' -> R.color.colorWarm
            'E' -> R.color.colorWarm
            else -> R.color.colorHint
        }
        val progress = if (value in 'A'.toFloat()..'E'.toFloat()) (nutritionalProgressBar?.max ?: 0) else 0
        changeNutritionalDisplay(R.string.nutritional_score, colorId, text, progress)
    }

    private fun changeNutritionalDisplay(nameId: Int, colorId: Int, value: String, progress: Int, isChild: Boolean = false) {
        nutritionalText?.setText(nameId)
        nutritionalValueText?.text = value
        nutritionalProgressBar?.progress = progress
        nutritionalProgressBar?.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorId))
        nutritionalProgressBar?.progressBackgroundTintList = nutritionalProgressBar?.progressTintList?.withAlpha(128)
        nutritionalValueLayout?.apply {
            (layoutParams as ViewGroup.MarginLayoutParams).also {
                it.leftMargin = if (isChild) context.resources.getDimension(R.dimen.margin_size).toInt() else 0
            }
        }
    }
}

