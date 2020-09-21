package com.epitech.diabetips.utils

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog

class DatePickerHandler(private val onDateSetListener: ((Int, Int, Int) -> Unit)): DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        onDateSetListener.invoke(year, monthOfYear, dayOfMonth)
    }

}

class TimePickerHandler(private val onTimeSetListener: ((Int, Int, Int) -> Unit)): TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        onTimeSetListener.invoke(hourOfDay, minute, second)
    }

}