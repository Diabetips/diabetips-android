package com.epitech.diabetips.activities

import android.os.Bundle
import android.widget.Toast
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.storages.InsulinCalculationObject
import com.epitech.diabetips.textWatchers.ItemClickWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.BarChartHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_average_glucose.averageBarChart
import kotlinx.android.synthetic.main.activity_insulin_quantity.*
import kotlin.math.roundToInt

class InsulinQuantityActivity : ADiabetipsActivity(R.layout.activity_insulin_quantity) {

    private val hourNumber: Int = 24
    private val hourDivider: Int = 3

    private var calculationOption = CalculationOptionObject(average = true, count = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeInsulinQuantityButton.setOnClickListener {
            finish()
        }
        insulinQuantityTimeDropdown.setAdapter(DropdownAdapter(this, R.array.time_range))
        insulinQuantityTimeDropdown.onItemClickListener = ItemClickWatcher((insulinQuantityTimeDropdown.adapter as DropdownAdapter).getItems()) { timeRange ->
            updateData(timeRange)
        }
        insulinQuantityTimeDropdown.setText(getString(R.string.time_range_month))
        updateData(insulinQuantityTimeDropdown.text.toString())
    }

    private fun updateData(timeRange: String) {
        calculationOption.setInterval(this, TimeHandler.instance.getIntervalFormat(this, timeRange, getString(R.string.format_time_api)))
        updateChart()
    }

    private fun updateChart() {
        InsulinService.instance.getCalculations(calculationOption).doOnSuccess {

            if (it.second.component2() == null) {
                val values = Array(hourNumber / hourDivider) { i: Int ->
                    var value = 0f
                    var valueDivider = 0f
                    for (j: Int in IntRange(0, hourDivider - 1)) {
                        if ((it.second.component1()?.average?.get(i * hourDivider + j)?.fast ?: 0f) != 0f) {
                            value += (it.second.component1()?.average?.get(i * hourDivider + j)?.fast ?: 0f)
                            ++valueDivider
                        }
                    }
                    Pair((i * hourDivider) + 0.25f + ((hourDivider - 1f) / 2f), value / (if (valueDivider != 0f) valueDivider else 1f))
                }
                BarChartHandler.handleBarChartStyle(averageBarChart, this)
                BarChartHandler.updateChartData(this, values, (hourNumber / (hourNumber / hourDivider)).toFloat() * 0.75f, averageBarChart)
                it.second.component1()?.let { it1 -> updateAverage(it1) }
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun updateAverage(insulinsCalculations: InsulinCalculationObject) {
        var average = 0f
        //Average injection quantity
        var count = 0f
        for (i in  0..hourNumber) {
            if ((insulinsCalculations.average.getOrNull(i)?.fast ?: 0f) != 0f) {
                average += insulinsCalculations.average.getOrNull(i)?.fast ?: 0f
                count += 1
            }
        }
        if (count != 0f) {
            average /= count
        }
        //Average daily injection quantity (days without data count as 0)
        /*for (i in  0..hourNumber) {
            average += (insulinsCalculations.average.getOrNull(i)?.fast ?: 0f) * (insulinsCalculations.count.getOrNull(i)?.fast ?: 0f)
        }
        average /= (TimeHandler.instance.getDayDiffFormat(insulinsCalculations.end, insulinsCalculations.start, getString(R.string.format_time_api)) + 1)*/
        insulinQuantityText.text = "${getText(R.string.average)} : ${average.roundToInt()} ${getString(R.string.unit)}"
    }
}
