package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.MealRecipeAdapter
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.storages.InsulinObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.RecipeObject
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.TimeHandler
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_new_entry.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*

class NewEntryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    enum class RequestCode {NEW_MEAL}

    private var slowInsulinId: Int = 0
    private var fastInsulinId: Int = 0
    private var saved: Boolean? = null
    private var entryTimestamp: Long = TimeHandler.instance.currentTimeSecond()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meal)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        newEntryTimeDate.setOnClickListener {
            TimeHandler.instance.getDatePickerDialog(this, this, entryTimestamp).show(supportFragmentManager, "DatePickerDialog")
        }
        newEntryTimeHour.setOnClickListener {
            TimeHandler.instance.getTimePickerDialog(this, this, entryTimestamp).show(supportFragmentManager, "TimePickerDialog")
        }
        newMealButton.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java), RequestCode.NEW_MEAL.ordinal)
        }
        saveNewEntryButton.setOnClickListener {
            saveEntry()
        }
        closeNewEntryButton.setOnClickListener {
            if (saved == null) {
                finish()
            } else if (saved!!) {
                finish()
            } else {
                val view = layoutInflater.inflate(R.layout.dialog_save_change, null)
                MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
                val dialog = AlertDialog.Builder(this@NewEntryActivity).setView(view).create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                view.saveChangeNegativeButton.setOnClickListener {
                    finish()
                }
                view.saveChangePositiveButton.setOnClickListener {
                    saveEntry(true)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun saveEntry(finishView: Boolean = false) {
        saveInsulin(getSlowInsulin())
        saveInsulin(getFastInsulin())
    }

    private fun getSlowInsulin() : InsulinObject {
        return InsulinObject(slowInsulinId,
            "",
            insulinSlowEntryInput.text.toString().toIntOrNull()?: 0,
            entryTimestamp,
            InsulinObject.InsulinType.slow.name)
    }

    private fun getFastInsulin() : InsulinObject {
       return InsulinObject(fastInsulinId,
            "",
            insulinFastEntryInput.text.toString().toIntOrNull()?: 0,
            entryTimestamp,
            InsulinObject.InsulinType.fast.name)
    }

    private fun saveInsulin(insulin: InsulinObject) {
        if (insulin.id <= 0 && insulin.quantity <= 0)
            return
        InsulinService.instance.createOrUpdateUserInsulin(insulin).doOnSuccess {
            if (it.second.component2() == null) {
                saved = true
                if (insulin.type == InsulinObject.InsulinType.slow.name)
                    slowInsulinId = it.second.component1()?.id!!
                else
                    fastInsulinId = it.second.component1()?.id!!
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.NEW_MEAL.ordinal) {
                saved = false
                //val meal = data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        entryTimestamp = TimeHandler.instance.changeTimestampDate(entryTimestamp, year, monthOfYear, dayOfMonth)
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        entryTimestamp = TimeHandler.instance.changeTimestampTime(entryTimestamp, hourOfDay, minute)
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
    }
}
