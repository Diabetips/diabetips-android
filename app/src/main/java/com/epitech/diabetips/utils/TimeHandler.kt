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
    val dayInSecond = 86400

    fun currentTimeSecond() : Long {
        return System.currentTimeMillis() / 1000
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimestampFromFormat(date: String, format: String) : Long? {
        if (date.isBlank())
            return null
        val dateVar = SimpleDateFormat(format).parse(date) ?: return null
        return dateVar.time / 1000
    }

    fun formatTimestamp(timestamp: Long, format: String): String {
        val date = Date(timestamp * 1000)
        return DateFormat.format(format, date).toString()
    }

    fun changeTimeFormat(date: String?, oldFormat: String, newFormat: String) : String? {
        if (date == null)
            return null
        val timestamp = getTimestampFromFormat(date, oldFormat) ?: return null
        return formatTimestamp(timestamp, newFormat)
    }

    fun getDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener, timestamp: Long): DatePickerDialog {
        calendar.timeInMillis = timestamp * 1000
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

    fun getTimePickerDialog(context: Context, timeSetListener: TimePickerDialog.OnTimeSetListener, timestamp: Long): TimePickerDialog {
        calendar.timeInMillis = timestamp * 1000
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

    fun getTimestampDate(year: Int, month: Int, day: Int): Long {
        return changeTimestampDate(currentTimeSecond(), year, month, day)
    }

    fun changeTimestampDate(timestamp: Long, year: Int, month: Int, day: Int): Long {
        calendar.timeInMillis = timestamp * 1000
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar.timeInMillis / 1000
    }

    fun changeTimestampTime(timestamp: Long, hour: Int, minute: Int): Long {
        calendar.timeInMillis = timestamp * 1000
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis / 1000
    }

    fun updateTimeDisplay(context: Context, timestamp: Long, dateDisplay: TextView? = null, timeDisplay: TextView? = null) {
        val date = Date(timestamp * 1000)
        dateDisplay?.text = DateFormat.format(context.getString(R.string.format_date), date)
        if (DateFormat.is24HourFormat(context)) {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_24), date)
        } else {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_12), date)
        }
    }

    fun updateDateTimeDisplay(context: Context, timestamp: Long, dateTimeDisplay: TextView) {
        val date = Date(timestamp * 1000)
        if (DateFormat.is24HourFormat(context)) {
            dateTimeDisplay.text = DateFormat.format(context.getString(R.string.format_date_simple_24), date)
        } else {
            dateTimeDisplay.text = DateFormat.format(context.getString(R.string.format_date_simple_12), date)
        }
    }
}