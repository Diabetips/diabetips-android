package com.epitech.diabetips.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.storages.BloodSugarRangesPercentageObject
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.storages.InsulinCalculationObject
import com.epitech.diabetips.textWatchers.ItemClickWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.PieChartHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_blood_sugar_ranges.*
import kotlin.random.Random
import kotlin.random.nextInt

class BloodSugarLevelRangesActivity : ADiabetipsActivity(R.layout.activity_blood_sugar_ranges) {

    private val hourNumber: Int = 24
    private val hourDivider: Int = 3

    private var calculationOption = CalculationOptionObject(average = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeBloodSugarRangesButton.setOnClickListener {
            finish()
        }
        bloodSugarRangesTimeDropdown.setAdapter(DropdownAdapter(this, R.array.time_range))
        bloodSugarRangesTimeDropdown.onItemClickListener = ItemClickWatcher((bloodSugarRangesTimeDropdown.adapter as DropdownAdapter).getItems()) { timeRange ->
            updateData(timeRange)
        }
        bloodSugarRangesTimeDropdown.setText(getString(R.string.time_range_month))
        updateData(bloodSugarRangesTimeDropdown.text.toString())
    }

    private fun updateData(timeRange: String) {
        updateChart(timeRange)
    }

    private fun updateChart(timeRange: String) {
        BloodSugarService.instance.getRangesPercentage(TimeHandler.instance.getIntervalFormat(this, timeRange, getString(R.string.format_time_api))).doOnSuccess {

            if (it.second.component2() == null && it.second.component1() != null) {
                var bloodSugarRanges: BloodSugarRangesPercentageObject = it.second.component1()!!;
                bloodSugarRanges.in_target = Random.nextInt(60, 80).toFloat();
                bloodSugarRanges.hyperglycemia = Random.nextInt(10, 100 - bloodSugarRanges.in_target!!.toInt()).toFloat();
                bloodSugarRanges.hypoglycemia = (100 - bloodSugarRanges.hyperglycemia!!.toInt() - bloodSugarRanges.in_target!!.toInt()).toFloat();

                PieChartHandler.handleBarChartStyle(averagePieChart, this)
                val points = mutableListOf(
                    Pair(bloodSugarRanges.in_target!!, ContextCompat.getColor(this, R.color.colorPrimary)),
                    Pair(bloodSugarRanges.hypoglycemia!!, ContextCompat.getColor(this, R.color.colorAccent)),
                    Pair(bloodSugarRanges.hyperglycemia!!, ContextCompat.getColor(this, R.color.colorBackgroundDarkGray)))
                PieChartHandler.updateChartData(this, averagePieChart, points.toTypedArray())
                hypoglycemia_target_text.text = bloodSugarRanges.hypoglycemia.toString();
                hyperglycemia_target_text.text = bloodSugarRanges.hyperglycemia.toString();
                in_target_text.text = bloodSugarRanges.in_target.toString();

//                val w = hypoglycemia_target_jauge.layoutParams.width;
//                Log.d("ouiii", w.toString());
//                hypoglycemia_target_jauge.layoutParams.width = (60f * w / 100f).toInt();
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }

    private fun updateAverage(insulinsCalculations: InsulinCalculationObject) {
    }
}
