package com.epitech.diabetips.storages

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import java.io.Serializable

data class DashboardItemObject (
    var id: Int = 0,
    var description: String = "",
    var title: String = "",
    var time: Long = 0,
    var icon: Drawable? = null) : Serializable {

    constructor (meal: MealObject, context: Context) : this(
        id = meal.id,
        description = meal.recipes.map { it.name }.joinToString(separator = ", "),
        title = context.resources.getString(R.string.meal),
        time = meal.timestamp,
        icon = context.getDrawable(R.drawable.ic_fork)) {
        icon!!.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    constructor (insulin: InsulinObject, context: Context) : this(
        id = insulin.id,
        description = insulin.quantity.toString() + " " + context.resources.getString(R.string.unit),
        title = if (insulin.type == "fast") context.resources.getString(R.string.insulin_fast) else context.resources.getString(R.string.insulin_slow),
        time = insulin.timestamp,
        icon = context.getDrawable(R.drawable.ic_syringe)) {
            icon!!.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
    }

    //Rest of the code in the class
}