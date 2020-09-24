package com.epitech.diabetips.storages

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.ObjectType
import java.io.Serializable

data class EntryObject (
    var id: Int = 0,
    var orignal: Any,
    var description: String = "",
    var title: String = "",
    var time: String = "",
    var icon: Drawable? = null) : Serializable {

    lateinit var type: ObjectType

    constructor (meal: MealObject, context: Context) : this(
        orignal = meal,
        id = meal.id,
        description = meal.getSummary(", "),
        title = context.getString(R.string.meal),
        time = meal.time,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_fork)) {
            type = ObjectType.MEAL
            icon?.setTint(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    constructor (insulin: InsulinObject, context: Context) : this(
        orignal = insulin,
        id = insulin.id,
        description = "${insulin.quantity} ${context.getString(R.string.unit)}",
        title = if (insulin.type == InsulinObject.Type.fast.toString()) context.getString(R.string.insulin_fast) else context.getString(R.string.insulin_slow),
        time = insulin.time,
        icon = ContextCompat.getDrawable(context, if (insulin.type == InsulinObject.Type.fast.toString()) R.drawable.ic_syringe else R.drawable.ic_syringe_alt)) {
            type = if (insulin.type == InsulinObject.Type.fast.toString()) ObjectType.INSULIN_FAST else ObjectType.INSULIN_SLOW
            icon?.setTint(ContextCompat.getColor(context, if (type == ObjectType.INSULIN_FAST) R.color.colorAccent else R.color.colorAccentLight))
    }

    constructor (note: NoteObject, context: Context) : this(
        orignal = note,
        id = note.id,
        description = note.description,
        title = context.getString(R.string.comment),
        time = note.time,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_comment)) {
            type = ObjectType.NOTE
            icon?.setTint(ContextCompat.getColor(context, R.color.searchBarSearchIconTintColor))
    }

    constructor (bloodSugar: BloodSugarObject, context: Context) : this(
        orignal = bloodSugar,
        id = 0,
        description = "${bloodSugar.value} ${context.getString(R.string.unit_u)}",
        title = context.getString(R.string.sugar),
        time = bloodSugar.time,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_syringe)) {
            type = ObjectType.SUGAR
            icon?.setTint(ContextCompat.getColor(context, R.color.colorAccent))
    }
}