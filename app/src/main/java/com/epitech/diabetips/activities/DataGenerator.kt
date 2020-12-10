package com.epitech.diabetips.activities

import android.os.Bundle
import android.util.Log
import com.epitech.diabetips.R
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.FakeDayDataGenerator
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.DialogHandler
import com.epitech.diabetips.utils.TimeHandler
import com.github.kittinunf.fuel.core.FuelManager
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

    private val injectionInterval: Int = 10;

    private fun generate()
    {
        val end = TimeHandler.instance.currentTime()
        val fullStart = TimeHandler.instance.getTimestampFromFormat(entryTime, getString(R.string.format_time_api));
        if (fullStart == null)
            return;
        val start = TimeHandler.instance.changeTimestampTime(fullStart, hour = 0, minute = 0)
        BloodSugarService.instance.deleteAllMeasures(
            TimeHandler.instance.formatTimestamp(start, getString(R.string.format_time_api_UTC)),
            TimeHandler.instance.formatTimestamp(end, getString(R.string.format_time_api_UTC))).doOnSuccess{
            GenerateData(start, true)
        }.subscribe()
    }
    private fun RemoveData()
    {
        TimeHandler.instance.getTimestampFromFormat(entryTime, getString(R.string.format_time_api))?.let {
            FakeDayDataGenerator().removeData(it, getString(R.string.format_time_api_UTC)).subscribe()
        };
    }

    private fun GenerateData(date: Long, trimData: Boolean = false)
    {
        BloodSugarService.instance.getLastMeasure().doOnSuccess{
            var startValue = 100f + Random.nextInt(-10, 10).toFloat()
            if (it.second.component2() == null)
                startValue = it.second.component1()!!.value.toFloat()

            TimeHandler.instance.getTimestampFromFormat(entryTime, getString(R.string.format_time_api))?.let {
                FakeDayDataGenerator().removeData(it, getString(R.string.format_time_api_UTC)).doOnSuccess{
                    var interatableDate = date;
                    val current = TimeHandler.instance.currentTime();
                    var i = 0;
                    while (interatableDate < current) {
                        if (i > 31)
                            return@doOnSuccess;
                        val fakeDayDataGenerator = FakeDayDataGenerator(injectionInterval, startValue, Random(1));
                        val points =  fakeDayDataGenerator.getDay();
                        fakeDayDataGenerator.sendData(points.toTypedArray(), TimeHandler.instance.changeTimestampTime(interatableDate, hour = 0, minute = 0), trimData, getString(R.string.format_time_api_UTC)).subscribe();
                        interatableDate = TimeHandler.instance.addTimeToTimestamp(date, 1, Calendar.DAY_OF_YEAR)
                        startValue = fakeDayDataGenerator.getLastValue();
                        i++;
                    }
                }.subscribe()
            };
        }.subscribe()
    }
}

class FloatPoint(var x: Float, var y: Float)