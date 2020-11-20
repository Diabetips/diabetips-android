package com.epitech.diabetips.storages

import android.content.Context
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.TimeHandler
import java.io.Serializable
import java.util.*

data class ActivityObject (
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var type: String = "",
    var intensity: Int = 0,
    var calories: Int = 1,
    var start: String = "",
    var end: String = "") : Serializable {

    fun setStart(context: Context, _start: String) {
        val duration = TimeHandler.instance.getFormatDiff(end, start, context.getString(R.string.format_time_api))
        start = _start
        setDuration(context, duration)
    }

    fun setDuration(context: Context, duration: String) {
        end = TimeHandler.instance.addTimeToFormat(
            start, context.getString(R.string.format_time_api),
            (TimeHandler.instance.getTimestampFromFormat(duration, context.getString(R.string.format_time_api), true)?.toInt() ?: 0), Calendar.MILLISECOND)
    }

    fun getDuration(context: Context): String {
        return TimeHandler.instance.getFormatDiff(end, start, context.getString(R.string.format_time_api), context.getString(R.string.format_hour_24))
    }

    fun getDurationSecond(context: Context): Long {
        return TimeHandler.instance.getSecondDiffFormat(end, start, context.getString(R.string.format_time_api))
    }

    fun getIntensity(context: Context): String {
        val intensities = context.resources.getStringArray(R.array.intensities)
        return intensities[intensity.coerceIn(0, intensities.size - 1)]
    }
}