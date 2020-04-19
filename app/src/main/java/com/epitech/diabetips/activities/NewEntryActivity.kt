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

    private val objects: MutableMap<ObjectType, Pair<Int, Boolean?>> = mutableMapOf()
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
        closeNewEntryButton.setOnClickListener {
            onBackPressed()
        }
        calculateInsulinButton.setOnClickListener {
            PredictionService.instance.getUserPrediction().doOnSuccess {
                if (it.second.component2() == null) {
                    calculateInsulinQuantity.text = it.second.component1()?.insulin.toString() + " " + getString(R.string.unit_units)
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
        objects[ObjectType.MEAL] = Pair<Int, Boolean?>(0, null)
        objects[ObjectType.SLOW_INSULIN] = Pair<Int, Boolean?>(0, null)
        objects[ObjectType.FAST_INSULIN] = Pair<Int, Boolean?>(0, null)
        objects[ObjectType.NOTE] = Pair<Int, Boolean?>(0, null)
        objects[ObjectType.EVENT] = Pair<Int, Boolean?>(0, null)
    }

    private fun addTextChangedListener() {
        insulinSlowEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.SLOW_INSULIN] = Pair(objects[ObjectType.SLOW_INSULIN]!!.first,
                if (((it.toString().toIntOrNull() ?: 0 > 0) || objects[ObjectType.SLOW_INSULIN]!!.first > 0)) false else null)
        })
        insulinFastEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.FAST_INSULIN] = Pair(objects[ObjectType.FAST_INSULIN]!!.first,
                if (((it.toString().toIntOrNull() ?: 0) > 0 || objects[ObjectType.FAST_INSULIN]!!.first > 0)) false else null)
        })
        commentEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.NOTE] = Pair(objects[ObjectType.NOTE]!!.first,
                if ((it.toString().isNotBlank() || objects[ObjectType.NOTE]!!.first > 0)) false else null)
        })
        physicalActivityEntryInput.addTextChangedListener(TextChangedWatcher {
            objects[ObjectType.EVENT] = Pair(objects[ObjectType.EVENT]!!.first,
                if (((it.toString().toIntOrNull() ?: 0) > 0 || objects[ObjectType.EVENT]!!.first > 0)) false else null)
        })
    }

    private fun updateMealDisplay() {
        if (meal.id > 0) {
            newMealButton.visibility = View.GONE
            entryMealCard.visibility = View.VISIBLE
            entryMealQuantity.text = getString(R.string.total_sugar) + " " + meal.total_sugar.toString() + " " + getString(R.string.unit_g)
        } else {
            newMealButton.visibility = View.VISIBLE
            entryMealCard.visibility = View.GONE
        }
    }

    private fun saveEntry(finishView: Boolean = false) {
        if (objects[ObjectType.SLOW_INSULIN]!!.second == false)
            saveInsulin(getSlowInsulin(), finishView)
        if (objects[ObjectType.FAST_INSULIN]!!.second == false)
            saveInsulin(getFastInsulin(), finishView)
        if (objects[ObjectType.NOTE]!!.second == false)
            saveNote(finishView)
        if (objects[ObjectType.EVENT]!!.second == false)
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
        InsulinService.instance.createOrUpdateUserInsulin(insulin).doOnSuccess {
            if (it.second.component2() == null) {
                if (insulin.type == InsulinObject.Type.slow.name)
                    objects[ObjectType.SLOW_INSULIN] = Pair<Int, Boolean?>(it.second.component1()?.id!!, true)
                else
                    objects[ObjectType.FAST_INSULIN] = Pair<Int, Boolean?>(it.second.component1()?.id!!, true)
                if (finishView)
                    tryToFinishView()
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun saveNote(finishView: Boolean = false) {
        val note = NoteObject(objects[ObjectType.NOTE]!!.first, commentEntryInput.text.toString(), entryTimestamp)
        NoteService.instance.createOrUpdateUserNote(note).doOnSuccess {
            if (it.second.component2() == null) {
                objects[ObjectType.NOTE] = Pair<Int, Boolean?>(it.second.component1()?.id!!, true)
                if (finishView)
                    tryToFinishView()
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun saveEvent(finishView: Boolean = false) {
        val event = EventObject(objects[ObjectType.EVENT]!!.first, "Activité Physique", entryTimestamp,
            entryTimestamp + (physicalActivityEntryInput.text.toString().toIntOrNull()?: 0) * 60)
        EventService.instance.createOrUpdateUserEvent(event).doOnSuccess {
            if (it.second.component2() == null) {
                objects[ObjectType.EVENT] = Pair<Int, Boolean?>(it.second.component1()?.id!!, true)
                if (finishView)
                    tryToFinishView()
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun tryToFinishView() {
        if (objects.all { obj -> obj.value.second != false })
            finish()
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
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        entryTimestamp = TimeHandler.instance.changeTimestampTime(entryTimestamp, hourOfDay, minute)
        TimeHandler.instance.updateTimeDisplay(this, entryTimestamp, newEntryTimeDate, newEntryTimeHour)
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
