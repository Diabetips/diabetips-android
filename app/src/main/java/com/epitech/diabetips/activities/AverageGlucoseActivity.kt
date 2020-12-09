package com.epitech.diabetips.activities

import android.os.Bundle
import android.widget.Toast
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.textWatchers.ItemClickWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.BarChartHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_average_glucose.*
import kotlin.math.roundToInt

class AverageGlucoseActivity : ADiabetipsActivity(R.layout.activity_average_glucose) {

    private val hourNumber: Int = 24
    private val hourDivider: Int = 3

    private var calculationOption = CalculationOptionObject(average = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeAverageGlucoseButton.setOnClickListener {
            finish()
        }
        averageGlucoseTimeDropdown.setAdapter(DropdownAdapter(this, R.array.time_range))
        averageGlucoseTimeDropdown.onItemClickListener = ItemClickWatcher((averageGlucoseTimeDropdown.adapter as DropdownAdapter).getItems()) { timeRange ->
            updateData(timeRange)
        }
        averageGlucoseTimeDropdown.setText(getString(R.string.time_range_month))
        updateData(averageGlucoseTimeDropdown.text.toString())
    }

    private fun updateData(timeRange: String) {
        calculationOption.setInterval(this, TimeHandler.instance.getIntervalFormat(this, timeRange, getString(R.string.format_time_api)))
        updateChart()
        updateAverage()
    }

    private fun updateChart() {
        BloodSugarService.instance.getAggregateCalculations(calculationOption).doOnSuccess {
            if (it.second.component2() == null) {
                val values = Array(hourNumber / hourDivider) { i: Int ->
                    var value = 0f
                    for (j: Int in IntRange(0, hourDivider - 1)) {
                        value += (it.second.component1()?.average?.get(i * hourDivider + j) ?: 0f)
                    }
                    Pair((i * hourDivider) + 0.25f + ((hourDivider - 1f) / 2f), value / hourDivider)
                }
                BarChartHandler.handleBarChartStyle(averageBarChart, this)
                BarChartHandler.updateChartData(this, values, (hourNumber / (hourNumber / hourDivider)).toFloat() * 0.75f, averageBarChart, R.string.no_data_period)
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun updateAverage() {
        BloodSugarService.instance.getCalculations(calculationOption).doOnSuccess {
            if (it.second.component2() == null) {
                val average: Float = it.second.component1()?.average ?: 0f
                if (average > 0f) {
                    averageGlucoseText.text = "${getText(R.string.average)} : ${average.roundToInt()} ${getString(R.string.unit_glucose)}"
                } else {
                    averageGlucoseText.text = ""
                }
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }

        }.subscribe()
    }
}
