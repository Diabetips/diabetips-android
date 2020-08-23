package com.epitech.diabetips.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
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

    fun currentTimeSecond() : Long {
        return System.currentTimeMillis() / 1000
    }

    fun currentTimeFormat(format: String) : String {
        return formatTimestamp(System.currentTimeMillis(), format)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimestampFromFormat(date: String, format: String) : Long? {
        if (date.isBlank())
            return null
        val dateVar = SimpleDateFormat(format).parse(date) ?: return null
        return dateVar.time
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimestamp(timestamp: Long, format: String): String {
        return SimpleDateFormat(format).format(timestamp)
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
        datePickerDialog.isThemeDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
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
        timePickerDialog.isThemeDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        timePickerDialog.accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
        return timePickerDialog
    }

    fun addMinuteToFormat(date: String, format: String, minuteToAdd: Int = 0): String {
        calendar.timeInMillis = getTimestampFromFormat(date, format) ?: currentTime()
        calendar.add(Calendar.MINUTE, minuteToAdd)
        return formatTimestamp(calendar.timeInMillis, format)
    }

    fun getSecondDiffFormat(start: String, end: String, format: String): Long {
        return ((getTimestampFromFormat(start, format) ?: currentTime()) - (getTimestampFromFormat(end, format) ?: 0)) / 1000
    }

    fun getTimestampDate(year: Int, month: Int, day: Int): Long {
        return changeTimestampDate(currentTimeSecond(), year, month, day)
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
}