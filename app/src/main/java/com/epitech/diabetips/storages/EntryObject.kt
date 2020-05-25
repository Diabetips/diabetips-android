package com.epitech.diabetips.storages

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import java.io.Serializable


data class EntryObject (
    var id: Int = 0,
    var orignal: Any,
    var description: String = "",
    var title: String = "",
    var time: Long = 0,
    var icon: Drawable? = null) : Serializable {

    lateinit var type: Type

    constructor (meal: MealObject, context: Context) : this(
        orignal = meal,
        id = meal.id,
        description = meal.recipes.map { it.recipe.name }.joinToString(separator = ", "),
        title = context.resources.getString(R.string.meal),
        time = meal.timestamp,
        icon = context.getDrawable(R.drawable.ic_fork)) {
            type = Type.MEAL
            icon!!.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    constructor (insulin: InsulinObject, context: Context) : this(
        orignal = insulin,
        id = insulin.id,
        description = insulin.quantity.toString() + " " + context.resources.getString(R.string.unit),
        title = if (insulin.type == "fast") context.resources.getString(R.string.insulin_fast) else context.resources.getString(R.string.insulin_slow),
        time = insulin.timestamp,
        icon = context.getDrawable(R.drawable.ic_syringe)) {
            type = if (insulin.type == "fast") Type.INSULIN_FAST else Type.INSULIN_SLOW
            icon!!.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
    }

    constructor (note: NoteObject, context: Context) : this(
        orignal = note,
        id = note.id,
        description = note.description,
        title = context.resources.getString(R.string.comment),
        time = note.timestamp,
        icon = context.getDrawable(R.drawable.ic_comment)) {
            type = Type.COMMENT
            icon!!.setColorFilter(ContextCompat.getColor(context, R.color.searchBarSearchIconTintColor), PorterDuff.Mode.SRC_ATOP)
    }


    constructor (bloodSugar: BloodSugarObject, context: Context) : this(
        orignal = bloodSugar,
        id = 0,
        description = bloodSugar.value.toString() + " u",
        title = "Sugar",
        time = bloodSugar.timestamp,
        icon = context.getDrawable(R.drawable.ic_syringe)) {
        type = Type.SUGAR
        icon!!.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
    }

    enum class Type {
        MEAL, COMMENT, INSULIN_SLOW, INSULIN_FAST, SUGAR, INSULIN
    }

    //Rest of the code in the class
}