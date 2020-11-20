package com.epitech.diabetips.activities

import android.os.Bundle
import android.widget.Toast
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.storages.InsulinCalculationObject
import com.epitech.diabetips.storages.InsulinObject
import com.epitech.diabetips.storages.InsulinsQuantityObject
import com.epitech.diabetips.textWatchers.ItemClickWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.BarChartHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_average_glucose.*
import kotlinx.android.synthetic.main.activity_average_glucose.averageBarChart
import kotlinx.android.synthetic.main.activity_insulin_quantity.*
import kotlin.random.Random

class InsulinQuantityActivity : ADiabetipsActivity(R.layout.activity_insulin_quantity) {

    private val hourNumber: Int = 24
    private val hourDivider: Int = 3

    private var calculationOption = CalculationOptionObject(average = true)

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
        calculationOption.setInterval(TimeHandler.instance.getIntervalFormat(this, timeRange, getString(R.string.format_time_api)))
        updateChart()
    }

    private fun updateChart() {
        InsulinService.instance.getCalculations(calculationOption).doOnSuccess {

            if (it.second.component2() == null) {
                //TODO Remove random
                print(it.second.component1())
                it.second.component1()?.average = Array(hourNumber) {
                    InsulinsQuantityObject(Random.nextDouble(0.0, 15.0).toFloat(),
                        Random.nextDouble(0.0, 15.0).toFloat(),
                        0f);
                }
                it.second.component1()?.count = Array(hourNumber) {
                    InsulinsQuantityObject(1f,
                        0f,
                        0f);
                }
                it.second.component1()?.count?.get(8)?.fast = 0.8f;
                it.second.component1()?.count?.get(11)?.fast = 0.1f;
                it.second.component1()?.count?.get(12)?.fast = 0.8f;
                it.second.component1()?.count?.get(13)?.fast = 0.2f;
                it.second.component1()?.count?.get(16)?.fast = 0.2f;
                it.second.component1()?.count?.get(19)?.fast = 1f;

                val values = Array(hourNumber / hourDivider) { i: Int ->
                    var value = 0f
                    for (j: Int in IntRange(0, hourDivider - 1)) {
                        value += (it.second.component1()?.average?.get( i * hourDivider + j)?.fast ?: 0f)
                    }
                    Pair((i * hourDivider) + 0.25f + ((hourDivider - 1f) / 2f), value / hourDivider)
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
        var average: Float = 0f;
        for (i in  0..hourNumber)   {
            if (i >= insulinsCalculations.average.count()) {
                break;
            }
            average += insulinsCalculations.average.get(i)?.fast!! * insulinsCalculations.count.get(i)?.fast!!;
        }
        insulinQuantityText.text = "${getText(R.string.average)} : ${average.toBigDecimal().stripTrailingZeros().toPlainString()} ${getString(R.string.unit)}"
    }
}
