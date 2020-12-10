package com.epitech.diabetips.activities

import android.os.Bundle
import com.epitech.diabetips.R
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.FakeDayDataGenerator
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_data_generator.*


class DataGenerator : ADiabetipsActivity(R.layout.activity_data_generator) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateButton.setOnClickListener{generate()}
    }

    private val injectionInterval: Int = 5;

    private fun generate()
    {
        val p = FakeDayDataGenerator(injectionInterval).getDay();
        val end = TimeHandler.instance.currentTime()
        val start = TimeHandler.instance.changeTimestampTime(end, 0, 0)
        BloodSugarService.instance.deleteAllMeasures(
            TimeHandler.instance.formatTimestamp(start, getString(R.string.format_time_api)),
            TimeHandler.instance.formatTimestamp(end, getString(R.string.format_time_api))).doOnSuccess{
            SendData(p, start)
        }.subscribe()
    }


    private fun SendData(points: Array<FloatPoint?>, date: Long)
    {
        val bs = BloodSugarObject();
        bs.start = TimeHandler.instance.formatTimestamp(date, getString(R.string.format_time_api))
        val elapsedTime = (TimeHandler.instance.currentTime() - (date - 3600 * 1000 * 5)) / 1000;
        bs.measures = points.filter { it!!.x * 3600 <= elapsedTime }.map{ it!!.y.toInt() }.toTypedArray()
        print(bs.measures);
        bs.interval = 5 * 60;
        BloodSugarService.instance.postMeasures(bs).doOnSuccess {
            if (it.second.component2() == null) {
                print(it.second.component1()!!.time);
            }
        }.subscribe()
    }

}

class FloatPoint(var x: Float, var y: Float)