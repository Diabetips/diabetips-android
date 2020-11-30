package com.epitech.diabetips.utils

import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.ModeManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class TimeHandler {

    private object Holder { val INSTANCE = TimeHandler() }

    companion object {
        val instance: TimeHandler by lazy { Holder.INSTANCE }
    }

    private val calendar: Calendar = Calendar.getInstance()
    val dayInMinute = 1440

    fun currentTime() : Long {
        return System.currentTimeMillis()
    }

    fun currentTimeFormat(format: String) : String {
        return formatTimestamp(System.currentTimeMillis(), format)
    }

    fun getTimestampFromFormat(date: String, format: String, standardize: Boolean = false) : Long? {
        if (date.isBlank())
            return null
        val dateVar: Date = SimpleDateFormat(format, Locale.getDefault()).parse(date) ?: return null
        if (standardize) {
            return dateVar.time + TimeZone.getDefault().getOffset(dateVar.time)
        }
        return dateVar.time
    }

    fun formatTimestamp(timestamp: Long, format: String, standardize: Boolean = false): String {
        if (standardize) {
            return SimpleDateFormat(format, Locale.getDefault()).format(timestamp - TimeZone.getDefault().getOffset(timestamp))
        }
        return SimpleDateFormat(format, Locale.getDefault()).format(timestamp)
    }

    fun changeTimeFormat(date: String?, oldFormat: String, newFormat: String) : String? {
        if (date == null)
            return null
        val timestamp = getTimestampFromFormat(date, oldFormat) ?: return null
        return formatTimestamp(timestamp, newFormat)
    }

    fun getDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener, time: String): DatePickerDialog {
        calendar.timeInMillis = getTimestampFromFormat(time, context.getString(R.string.format_time_api)) ?: currentTime()
        val datePickerDialog = DatePickerDialog.newInstance(
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.version = DatePickerDialog.Version.VERSION_1
        datePickerDialog.isThemeDark = (ModeManager.instance.getDarkMode(context) == AppCompatDelegate.MODE_NIGHT_YES)
        datePickerDialog.accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
        calendar.time = Calendar.getInstance().time
        calendar.add(Calendar.DATE, 1)
        datePickerDialog.maxDate = calendar
        return datePickerDialog
    }

    fun getTimePickerDialog(context: Context, timeSetListener: TimePickerDialog.OnTimeSetListener, time: String): TimePickerDialog {
        calendar.timeInMillis = getTimestampFromFormat(time, context.getString(R.string.format_time_api)) ?: currentTime()
        val timePickerDialog = TimePickerDialog.newInstance(
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(context)
        )
        timePickerDialog.version = TimePickerDialog.Version.VERSION_2
        timePickerDialog.isThemeDark = (ModeManager.instance.getDarkMode(context) == AppCompatDelegate.MODE_NIGHT_YES)
        timePickerDialog.accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
        return timePickerDialog
    }

    fun addTimeToFormat(date: String, format: String, timeToAdd: Int = 0, timeType: Int = Calendar.MINUTE): String {
        return formatTimestamp(addTimeToTimestamp((getTimestampFromFormat(date, format) ?: currentTime()), timeToAdd, timeType), format)
    }

    fun addTimeToTimestamp(timestamp: Long, timeToAdd: Int = 0, timeType: Int = Calendar.MINUTE): Long {
        calendar.timeInMillis = timestamp
        calendar.add(timeType, timeToAdd)
        return calendar.timeInMillis
    }

    fun getSecondDiffFormat(start: String, end: String, format: String): Long {
        return getTimeDiffFormat(start, end, format) / 1000
    }

    fun getSecondDiff(start: Long, end: Long): Long {
        return getTimeDiff(start, end) / 1000
    }

    fun getFormatDiff(start: String, end: String, inputFormat: String, outputFormat: String = inputFormat): String {
        return formatTimestamp(getTimeDiffFormat(start, end, inputFormat), outputFormat, true)
    }

    private fun getTimeDiffFormat(start: String, end: String, format: String): Long {
        return getTimeDiff(getTimestampFromFormat(start, format) ?: currentTime(), getTimestampFromFormat(end, format) ?: currentTime())
    }

    private fun getTimeDiff(start: Long, end: Long): Long {
        return (start - end)
    }

    fun getIntervalFormat(context: Context, timeRange: String, format: String): Pair<String, String> {
        val current = currentTimeFormat(format)
        return when (timeRange) {
            context.getString(R.string.time_range_week) -> Pair(addTimeToFormat(current, format, -1, Calendar.WEEK_OF_YEAR), current)
            context.getString(R.string.time_range_week_2) -> Pair(addTimeToFormat(current, format, -2, Calendar.WEEK_OF_YEAR), current)
            context.getString(R.string.time_range_month) -> Pair(addTimeToFormat(current, format, -1, Calendar.MONTH), current)
            context.getString(R.string.time_range_month_3) -> Pair(addTimeToFormat(current, format, -3, Calendar.MONTH), current)
            context.getString(R.string.time_range_year) -> Pair(addTimeToFormat(current, format, -1, Calendar.YEAR), current)
            else -> Pair(formatTimestamp(0, format), current)
        }
    }

    fun changeFormatDate(date: String, format: String, year: Int, month: Int, day: Int): String {
        return formatTimestamp(changeTimestampDate(getTimestampFromFormat(date, format) ?: currentTime(), year, month, day), format)
    }

    fun changeFormatTime(date: String, format: String, hour: Int, minute: Int): String {
        return formatTimestamp(changeTimestampTime(getTimestampFromFormat(date, format) ?: currentTime(), hour, minute), format)
    }

    fun changeTimestampDate(timestamp: Long, year: Int, month: Int, day: Int): Long {
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar.timeInMillis
    }

    fun changeTimestampTime(timestamp: Long, hour: Int, minute: Int): Long {
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }

    fun updateTimeDisplay(context: Context, time: String, dateDisplay: TextView? = null, timeDisplay: TextView? = null) {
        val date = Date(getTimestampFromFormat(time, context.getString(R.string.format_time_api)) ?: currentTime())
        dateDisplay?.text = DateFormat.format(context.getString(R.string.format_date), date)
        if (DateFormat.is24HourFormat(context)) {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_24), date)
        } else {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_12), date)
        }
    }

    fun updateDateTimeDisplay(context: Context, time: String, dateTimeDisplay: TextView) {
        val date = Date(getTimestampFromFormat(time, context.getString(R.string.format_time_api)) ?: currentTime())
        if (DateFormat.is24HourFormat(context)) {
            dateTimeDisplay.text = DateFormat.format(context.getString(R.string.format_date_simple_24), date)
        } else {
            dateTimeDisplay.text = DateFormat.format(context.getString(R.string.format_date_simple_12), date)
        }
    }

    fun trimTimestamp(timestamp: Long) : Long {
        return timestamp - timestamp % (24 * 60 * 60 * 1000);
    }
}