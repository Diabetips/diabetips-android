package com.epitech.diabetips.storages

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.ObjectType
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_new_entry.*
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

    constructor (activity: ActivityObject, context: Context) : this(
        orignal = activity,
        id = activity.id,
        description ="${activity.getDuration(context)} • ${activity.type} • ${activity.getIntensity(context)}",
        title = context.getString(R.string.activity),
        time = activity.start,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_activity)) {
            type = ObjectType.ACTIVITY
            icon?.setTint(ContextCompat.getColor(context, R.color.colorGreen))
    }

    constructor (note: NoteObject, context: Context) : this(
        orignal = note,
        id = note.id,
        description = note.description,
        title = context.getString(R.string.comment),
        time = note.time,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_comment)) {
            type = ObjectType.NOTE
            icon?.setTint(MaterialHandler.getColorFromAttribute(context, R.attr.colorComment))
    }

    constructor (bloodSugar: BloodSugarObject, context: Context) : this(
        orignal = bloodSugar,
        id = 0,
        description = "${bloodSugar.value} ${context.getString(R.string.unit_glucose)}",
        title = context.getString(R.string.sugar),
        time = bloodSugar.time,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_syringe)) {
            type = ObjectType.SUGAR
            icon?.setTint(ContextCompat.getColor(context, R.color.colorWarm))
    }

    constructor (event: EventObject, context: Context) : this(
        orignal = event,
        id = event.id,
        description = event.description,
        title = context.getString(R.string.activity),
        time = event.start,
        icon = ContextCompat.getDrawable(context, R.drawable.ic_activity)) {
            type = ObjectType.ACTIVITY
            icon?.setTint(ContextCompat.getColor(context, R.color.colorPurple))
    }
}