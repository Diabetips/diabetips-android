package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.epitech.diabetips.R
import com.epitech.diabetips.services.EventService
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.services.NoteService
import com.epitech.diabetips.services.PredictionService
import com.epitech.diabetips.storages.EventObject
import com.epitech.diabetips.storages.InsulinObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.NoteObject
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.TimeHandler
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_new_entry.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*

class NewEntryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    enum class RequestCode {NEW_MEAL, UPDATE_MEAL}
    enum class ObjectType {MEAL, SLOW_INSULIN, FAST_INSULIN, NOTE, EVENT}

    private val objects: MutableMap<ObjectType, Triple<Int, Boolean?, Boolean>> = mutableMapOf()
    private var entryTimestamp: Long = TimeHandler.instance.currentTimeSecond()
    private var meal: MealObject = MealObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)
        MaterialHandler.instance.handleTextInputLayoutSize(this.findViewById(android.R.id.content))
        initObjectMap()
        addTextChangedListener()
        newEntryTimeDate.setOnClickListener {
            TimeHandler.instance.getDatePickerDialog(this, this, entryTimestamp).show(supportFragmentManager, "DatePickerDialog")
        }
        newEntryTimeHour.setOnClickListener {
            TimeHandler.instance.getTimePickerDialog(this, this, entryTimestamp).show(supportFragmentManager, "TimePickerDialog")
        }
        newMealButton.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java), RequestCode.NEW_MEAL.ordinal)
        }
        entryMealCard.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java)
                .putExtra(getString(R.string.param_meal), meal), RequestCode.UPDATE_MEAL.ordinal)
        }
        saveNewEntryButton.setOnClickListener {
            saveEntry()
        }
        validateNewEntryButton.setOnClickListener {
            saveEntry(true)
        }
        closeNewEntryButton.setOnClickListener {
            onBackPressed()
        }
        calculateInsulinButton.setOnClickListener {
            PredictionService.instance.getUserPrediction().doOnSuccess {
                if (it.second.component2() == null) {
                    calculateInsulinQuantity.text = "${it.second.component1()?.insulin} ${getString(R.string.unit_units)}"
                    calculateInsulinQuantity.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
        PredictionService.instance.getUserPredictionSettings().doOnSuccess {
            if (it.second.component2() == null && it.second.component1()?.enabled == true) {
                calculateInsulinButton.visibility = View.VISIBLE
            }
        }.subscribe()
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
    }

    private fun initObjectMap() {
        objects[ObjectType.MEAL] = Triple<Int, Boolean?, Boolean>(0, null, false)
        objects[ObjectType.SLOW_INSULIN] = Triple<Int, Boolean?, Boolean>(0, null, false)
        objects[ObjectType.FAST_INSULIN] = Triple<Int, Boolean?, Boolean>(0, null, false)
        objects[ObjectType.NOTE] = Triple<Int, Boolean?, Boolean>(0, null, false)
        objects[ObjectType.EVENT] = Triple<Int, Boolean?, Boolean>(0, null, false)
    }

    private fun addTextChangedListener() {
        insulinSlowEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.SLOW_INSULIN] = Triple(objects[ObjectType.SLOW_INSULIN]!!.first,
                if (((it.toString().toIntOrNull() ?: 0 > 0) || objects[ObjectType.SLOW_INSULIN]!!.first > 0)) false else null, false)
        })
        insulinFastEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.FAST_INSULIN] = Triple(objects[ObjectType.FAST_INSULIN]!!.first,
                if (((it.toString().toIntOrNull() ?: 0) > 0 || objects[ObjectType.FAST_INSULIN]!!.first > 0)) false else null, false)
        })
        commentEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.NOTE] = Triple(objects[ObjectType.NOTE]!!.first,
                if ((it.toString().isNotBlank() || objects[ObjectType.NOTE]!!.first > 0)) false else null, false)
        })
        physicalActivityEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.EVENT] = Triple(objects[ObjectType.EVENT]!!.first,
                if (((it.toString().toIntOrNull() ?: 0) > 0 || objects[ObjectType.EVENT]!!.first > 0)) false else null, false)
        })
    }

    private fun updateMealDisplay() {
        if (meal.id > 0) {
            newMealButton.text = getString(R.string.add_other_meal)
            entryMealCard.visibility = View.VISIBLE
            entryMealQuantity.text = "${meal.total_sugar} ${getString(R.string.unit_g)}"
            entryMealSummary.text = meal.getSummary()
            TimeHandler.instance.updateDateTimeDisplay(this, meal.timestamp, entryMealTime)
        } else {
            newMealButton.text = getString(R.string.add_meal)
            entryMealCard.visibility = View.GONE
        }
    }

    private fun saveEntry(finishView: Boolean = false) {
        if (objects[ObjectType.SLOW_INSULIN]!!.second == false && !objects[ObjectType.SLOW_INSULIN]!!.third)
            saveInsulin(getSlowInsulin(), finishView)
        if (objects[ObjectType.FAST_INSULIN]!!.second == false && !objects[ObjectType.FAST_INSULIN]!!.third)
            saveInsulin(getFastInsulin(), finishView)
        if (objects[ObjectType.NOTE]!!.second == false && !objects[ObjectType.NOTE]!!.third)
            saveNote(finishView)
        if (objects[ObjectType.EVENT]!!.second == false && !objects[ObjectType.EVENT]!!.third)
            saveEvent(finishView)
    }

    private fun getSlowInsulin() : InsulinObject {
        return InsulinObject(objects[ObjectType.SLOW_INSULIN]!!.first,
            "",
            insulinSlowEntryInput.text.toString().toIntOrNull()?: 0,
            entryTimestamp,
            InsulinObject.Type.slow.name)
    }

    private fun getFastInsulin() : InsulinObject {
       return InsulinObject(objects[ObjectType.FAST_INSULIN]!!.first,
            "",
            insulinFastEntryInput.text.toString().toIntOrNull()?: 0,
            entryTimestamp,
            InsulinObject.Type.fast.name)
    }

    private fun saveInsulin(insulin: InsulinObject, finishView: Boolean = false) {
        val objectType: ObjectType = if (insulin.type == InsulinObject.Type.slow.name) ObjectType.SLOW_INSULIN else ObjectType.FAST_INSULIN
        objects[objectType] = objects[ObjectType.SLOW_INSULIN]!!.copy(third = true)
        InsulinService.instance.createOrUpdate(insulin, insulin.id).doOnSuccess {
            if (it.second.component2() == null) {
                objects[objectType] = Triple<Int, Boolean?, Boolean>(it.second.component1()?.id!!, true, false)
                displaySavedMessage(finishView)
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                objects[objectType] = objects[ObjectType.SLOW_INSULIN]!!.copy(third = false)
            }
        }.subscribe()
    }

    private fun saveNote(finishView: Boolean = false) {
        objects[ObjectType.NOTE] = objects[ObjectType.NOTE]!!.copy(third = true)
        val note = NoteObject(objects[ObjectType.NOTE]!!.first, commentEntryInput.text.toString(), entryTimestamp)
        NoteService.instance.createOrUpdate(note, note.id).doOnSuccess {
            if (it.second.component2() == null) {
                objects[ObjectType.NOTE] = Triple<Int, Boolean?, Boolean>(it.second.component1()?.id!!, true, false)
                displaySavedMessage(finishView)
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                objects[ObjectType.NOTE] = objects[ObjectType.NOTE]!!.copy(third = false)
            }
        }.subscribe()
    }

    private fun saveEvent(finishView: Boolean = false) {
        objects[ObjectType.NOTE] = objects[ObjectType.NOTE]!!.copy(third = true)
        val event = EventObject(objects[ObjectType.EVENT]!!.first, "Activité Physique", entryTimestamp,
            entryTimestamp + (physicalActivityEntryInput.text.toString().toIntOrNull()?: 0) * 60)
        EventService.instance.createOrUpdate(event, event.id).doOnSuccess {
            if (it.second.component2() == null) {
                objects[ObjectType.EVENT] = Triple<Int, Boolean?, Boolean>(it.second.component1()?.id!!, true, false)
                displaySavedMessage(finishView)
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                objects[ObjectType.NOTE] = objects[ObjectType.NOTE]!!.copy(third = false)
            }
        }.subscribe()
    }

    private fun displaySavedMessage(finishView: Boolean) {
        if (objects.all { obj -> obj.value.second != false }) {
            Toast.makeText(this, getString(R.string.saved_change), Toast.LENGTH_SHORT).show()
            if (finishView)
                finish()
        }
    }

    private fun updateTimeDisplay() {
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && (requestCode == RequestCode.NEW_MEAL.ordinal
                    || requestCode == RequestCode.UPDATE_MEAL.ordinal)) {
                meal = data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject
                updateMealDisplay()
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        entryTimestamp = TimeHandler.instance.changeTimestampDate(entryTimestamp, year, monthOfYear, dayOfMonth)
        updateTimeDisplay()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        entryTimestamp = TimeHandler.instance.changeTimestampTime(entryTimestamp, hourOfDay, minute)
        updateTimeDisplay()
    }

    override fun onBackPressed() {
        if (objects.all { obj -> obj.value.second != false }) {
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
