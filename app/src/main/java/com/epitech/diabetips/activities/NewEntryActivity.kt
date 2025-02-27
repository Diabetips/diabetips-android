package com.epitech.diabetips.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.epitech.diabetips.R
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.services.NoteService
import com.epitech.diabetips.services.PredictionService
import com.epitech.diabetips.storages.*
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.activity_new_entry.*
import org.json.JSONObject
import java.lang.Exception
import java.nio.charset.Charset

class NewEntryActivity : ADiabetipsActivity(R.layout.activity_new_entry) {

    private val objects: MutableMap<ObjectType, Triple<Int, Boolean?, Boolean>> = mutableMapOf()
    private var entryTime: String = ""
    private var meal: MealObject = MealObject()
    private var activity: ActivityObject = ActivityObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryTime = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        initObjectMap()
        addTextChangedListener()
        newEntryTimeDate.setOnClickListener {
            DialogHandler.datePickerDialog(this, supportFragmentManager, entryTime, newEntryTimeDate) { time ->
                entryTime = time
            }
        }
        newEntryTimeHour.setOnClickListener {
            DialogHandler.timePickerDialog(this, supportFragmentManager, entryTime, newEntryTimeHour) { time ->
                entryTime = time
            }
        }
        newMealButton.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java)
                .putExtra(getString(R.string.param_meal), MealObject(time = entryTime)), RequestCode.NEW_MEAL.ordinal)
        }
        entryMealCard.setOnClickListener {
            startActivityForResult(Intent(this, NewMealActivity::class.java)
                .putExtra(getString(R.string.param_meal), meal), RequestCode.UPDATE_MEAL.ordinal)
        }
        newActivityButton.setOnClickListener {
            DialogHandler.dialogActivity(this, layoutInflater, supportFragmentManager, ActivityObject(start = entryTime, end = entryTime)) { activityObject  ->
                activity = activityObject ?: ActivityObject()
                updateActivityDisplay()
            }
        }
        activityEntryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activityEntryInput.clearFocus()
                DialogHandler.dialogActivity(this, layoutInflater, supportFragmentManager, activity) { activityObject  ->
                    activity = activityObject ?: ActivityObject()
                    updateActivityDisplay()
                }
            }
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
        handlePrediction()
        TimeHandler.instance.updateTimeDisplay(this, entryTime, newEntryTimeDate, newEntryTimeHour)
    }

    private fun handlePrediction() {
        calculateInsulinButton.setOnClickListener {
            PredictionService.instance.getUserPrediction().doOnSuccess {
                if (it.second.component2() == null) {
                    updatePrediction(it.second.component1())
                } else {
                    val error = it.first.data.toString(Charset.defaultCharset())
                    if (error.isNotBlank()) {
                        try {
                            Toast.makeText(this, JSONObject(error).getString("message"), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.subscribe()
        }
        PredictionService.instance.getUserPredictionSettings().doOnSuccess {
            if (it.second.component2() == null && it.second.component1()?.enabled == true) {
                calculateInsulinButton.visibility = View.VISIBLE
            }
        }.subscribe()
        updatePrediction(PredictionService.lastPrediction)
    }

    private fun updatePrediction(prediction: PredictionObject?) {
        PredictionService.lastPrediction = prediction
        if ((prediction?.id ?: 0) > 0) {
            calculateInsulinResult.text = "${prediction!!.insulin.toBigDecimal().stripTrailingZeros().toPlainString()} ${getString(R.string.unit_units)}"
            TimeHandler.instance.updateTimeDisplay(this, prediction.time, null, calculateInsulinResultTime)
            calculateInsulinResultTime.text = "${calculateInsulinResultTime.text} ${getString(R.string.last_prediction)}"
            calculateInsulinResultLayout.visibility = View.VISIBLE
        } else {
            calculateInsulinResultLayout.visibility = View.GONE
        }
    }

    private fun initObjectMap() {
        changeObjectMapValue(ObjectType.INSULIN_SLOW, Triple<Int, Boolean?, Boolean>(0, null, false))
        changeObjectMapValue(ObjectType.INSULIN_FAST, Triple<Int, Boolean?, Boolean>(0, null, false))
        changeObjectMapValue(ObjectType.NOTE, Triple<Int, Boolean?, Boolean>(0, null, false))
    }

    private fun changeObjectMapValue(objectType: ObjectType, value: Triple<Int, Boolean?, Boolean>) {
        objects[objectType] = value
        updateValidateDisplay()
    }

    private fun addTextChangedListener() {
        insulinSlowEntryInput.addTextChangedListener(TextChangedWatcher {
            changeObjectMapValue(ObjectType.INSULIN_SLOW, Triple(objects[ObjectType.INSULIN_SLOW]!!.first,
                if (((it.toString().toIntOrNull() ?: 0 > 0) || objects[ObjectType.INSULIN_SLOW]!!.first > 0)) false else null, false))
        })
        insulinFastEntryInput.addTextChangedListener(TextChangedWatcher {
            changeObjectMapValue(ObjectType.INSULIN_FAST, Triple(objects[ObjectType.INSULIN_FAST]!!.first,
                if (((it.toString().toIntOrNull() ?: 0) > 0 || objects[ObjectType.INSULIN_FAST]!!.first > 0)) false else null, false))
        })
        commentEntryInput.addTextChangedListener(TextChangedWatcher {
            changeObjectMapValue(ObjectType.NOTE, Triple(objects[ObjectType.NOTE]!!.first,
                if ((it.toString().isNotBlank() || objects[ObjectType.NOTE]!!.first > 0)) false else null, false))
        })
    }

    private fun updateMealDisplay() {
        if (meal.id > 0) {
            newMealButton.text = getString(R.string.add_other_meal)
            entryMealCard.visibility = View.VISIBLE
            entryMealQuantity.text = "${meal.total_carbohydrates.toBigDecimal().stripTrailingZeros().toPlainString()} ${getString(R.string.unit_g)}"
            entryMealSummary.text = meal.getSummary()
            TimeHandler.instance.updateDateTimeDisplay(this, meal.time, entryMealTime)
        } else {
            newMealButton.text = getString(R.string.add_meal)
            entryMealCard.visibility = View.GONE
        }
        updateValidateDisplay()
    }

    private fun updateActivityDisplay() {
        if (activity.id > 0) {
            newActivityButton.visibility = View.GONE
            activityEntryInputLayout.visibility = View.VISIBLE
            activityEntryInput.setText("${activity.getDuration(this)} • ${activity.type} • ${activity.getIntensity(this)}")
        } else {
            newActivityButton.visibility = View.VISIBLE
            activityEntryInputLayout.visibility = View.GONE
        }
        updateValidateDisplay()
    }

    private fun updateValidateDisplay() {
        validateNewEntryButton.isEnabled = (meal.id > 0 || activity.id > 0 || objects.any { obj -> obj.value.second == false })
    }

    private fun saveEntry(finishView: Boolean = false) {
        if (objects[ObjectType.INSULIN_SLOW]!!.second == false && !objects[ObjectType.INSULIN_SLOW]!!.third)
            saveInsulin(getSlowInsulin(), finishView)
        if (objects[ObjectType.INSULIN_FAST]!!.second == false && !objects[ObjectType.INSULIN_FAST]!!.third)
            saveInsulin(getFastInsulin(), finishView)
        if (objects[ObjectType.NOTE]!!.second == false && !objects[ObjectType.NOTE]!!.third)
            saveNote(finishView)
        if (meal.id > 0 || activity.id > 0)
            displaySavedMessage(finishView)
    }

    private fun getSlowInsulin() : InsulinObject {
        return InsulinObject(objects[ObjectType.INSULIN_SLOW]!!.first,
            "",
            insulinSlowEntryInput.text.toString().toIntOrNull()?: 0,
            entryTime,
            InsulinObject.Type.slow.name)
    }

    private fun getFastInsulin() : InsulinObject {
       return InsulinObject(objects[ObjectType.INSULIN_FAST]!!.first,
            "",
            insulinFastEntryInput.text.toString().toIntOrNull()?: 0,
            entryTime,
            InsulinObject.Type.fast.name)
    }

    private fun saveInsulin(insulin: InsulinObject, finishView: Boolean = false) {
        val objectType: ObjectType = if (insulin.type == InsulinObject.Type.slow.name) ObjectType.INSULIN_SLOW else ObjectType.INSULIN_FAST
        objects[objectType] = objects[objectType]!!.copy(third = true)
        InsulinService.instance.createOrUpdate(insulin, insulin.id).doOnSuccess {
            if (it.second.component2() == null) {
                changeObjectMapValue(objectType, Triple<Int, Boolean?, Boolean>(it.second.component1()?.id!!, true, false))
                displaySavedMessage(finishView)
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                objects[objectType] = objects[ObjectType.INSULIN_SLOW]!!.copy(third = false)
            }
        }.subscribe()
    }

    private fun saveNote(finishView: Boolean = false) {
        objects[ObjectType.NOTE] = objects[ObjectType.NOTE]!!.copy(third = true)
        val note = NoteObject(objects[ObjectType.NOTE]!!.first, commentEntryInput.text.toString(), entryTime)
        NoteService.instance.createOrUpdate(note, note.id).doOnSuccess {
            if (it.second.component2() == null) {
                changeObjectMapValue(ObjectType.NOTE, Triple<Int, Boolean?, Boolean>(it.second.component1()?.id!!, true, false))
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
            if (finishView) {
                setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.param_entry), true))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && (requestCode == RequestCode.NEW_MEAL.ordinal || requestCode == RequestCode.UPDATE_MEAL.ordinal)) {
                meal = (data?.getSerializableExtra(getString(R.string.param_meal)) as MealObject?) ?: MealObject()
                updateMealDisplay()
        }
    }

    override fun onBackPressed() {
        if (objects.all { obj -> obj.value.second != false }) {
            finish()
        } else {
            DialogHandler.dialogSaveChange(this, layoutInflater, { saveEntry(true) }, { finish() })
        }
    }
}
