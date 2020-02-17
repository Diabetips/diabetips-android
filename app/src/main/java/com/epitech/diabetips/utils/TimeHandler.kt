package com.epitech.diabetips.utils

import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*


class TimeHandler {

    private object Holder { val INSTANCE = TimeHandler() }

    companion object {
        val instance: TimeHandler by lazy { Holder.INSTANCE }
    }

    private val calendar = Calendar.getInstance()

    fun getDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener, timestamp: Long): DatePickerDialog {
        calendar.timeInMillis = timestamp
        val datePickerDialog = DatePickerDialog.newInstance(
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.version = DatePickerDialog.Version.VERSION_1
        datePickerDialog.isThemeDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        datePickerDialog.accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
        return datePickerDialog
    }

    fun getTimePickerDialog(context: Context, timeSetListener: TimePickerDialog.OnTimeSetListener, timestamp: Long): TimePickerDialog {
        calendar.timeInMillis = timestamp
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

    fun updateTimeDisplay(context: Context, timestamp: Long, dateDisplay: TextView? = null, timeDisplay: TextView? = null) {
        dateDisplay?.text = DateFormat.format(context.getString(R.string.format_date), Date(timestamp))
        if (DateFormat.is24HourFormat(context)) {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_24), Date(timestamp))
        } else {
            timeDisplay?.text = DateFormat.format(context.getString(R.string.format_hour_12), Date(timestamp))
        }
    }
}