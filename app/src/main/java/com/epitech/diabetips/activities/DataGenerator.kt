package com.epitech.diabetips.activities

import android.os.Bundle
import com.epitech.diabetips.R
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.FakeDayDataGenerator
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.DialogHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_data_generator.*
import java.util.*
import kotlin.random.Random


class DataGenerator : ADiabetipsActivity(R.layout.activity_data_generator) {
    private var entryTime: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateButton.setOnClickListener{generate()}
        deleteButton.setOnClickListener{RemoveData()}

        entryTime = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        date_picker_from.setOnClickListener {
            DialogHandler.datePickerDialog(this, supportFragmentManager, entryTime, date_picker_from) { time ->
                entryTime = time
            }
        }
        TimeHandler.instance.updateTimeDisplay(this, entryTime, date_picker_from)
    }

    private val injectionInterval: Int = 5;

    private fun generate()
    {
        val end = TimeHandler.instance.currentTime()
        val fullStart = TimeHandler.instance.getTimestampFromFormat(entryTime, getString(R.string.format_time_api));
        if (fullStart == null)
            return;
        val start = TimeHandler.instance.changeTimestampTime(fullStart, 0, 0)
        BloodSugarService.instance.deleteAllMeasures(
            TimeHandler.instance.formatTimestamp(start, getString(R.string.format_time_api)),
            TimeHandler.instance.formatTimestamp(end, getString(R.string.format_time_api))).doOnSuccess{
            GenerateData(start, true)
        }.subscribe()
    }
    private fun RemoveData()
    {
        val end = TimeHandler.instance.currentTime()
        val fullStart = TimeHandler.instance.getTimestampFromFormat(entryTime, getString(R.string.format_time_api));
        if (fullStart == null)
            return;
        val start = TimeHandler.instance.changeTimestampTime(fullStart, 0, 0)
        BloodSugarService.instance.deleteAllMeasures(
            TimeHandler.instance.formatTimestamp(start, getString(R.string.format_time_api)),
            TimeHandler.instance.formatTimestamp(end, getString(R.string.format_time_api))).doOnSuccess{
        }.subscribe()
    }

    private fun GenerateData(date: Long, trimData: Boolean = false)
    {
        var interatableDate = date;
        val current = TimeHandler.instance.currentTime();
        var lastStart = 100f + Random.nextInt(-10, 10).toFloat();
        var i = 0;
        while (interatableDate < current) {
            if (i > 31)
                return;
            val fakeDayDataGenerator = FakeDayDataGenerator(injectionInterval, lastStart, Random(1));
            val points =  fakeDayDataGenerator.getDay();
            fakeDayDataGenerator.sendData(points, interatableDate, trimData, getString(R.string.format_time_api)).subscribe();
            interatableDate = TimeHandler.instance.addTimeToTimestamp(date, 1, Calendar.DAY_OF_YEAR)
            lastStart = fakeDayDataGenerator.getLastValue();
            i++;
        }
    }
}

class FloatPoint(var x: Float, var y: Float)