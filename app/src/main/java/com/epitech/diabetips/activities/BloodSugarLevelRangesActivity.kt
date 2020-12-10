package com.epitech.diabetips.activities

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.storages.BloodSugarRangesPercentageObject
import com.epitech.diabetips.storages.CalculationOptionObject
import com.epitech.diabetips.textWatchers.ItemClickWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.PieChartHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_blood_sugar_ranges.*

class BloodSugarLevelRangesActivity : ADiabetipsActivity(R.layout.activity_blood_sugar_ranges) {

    private var calculationOption = CalculationOptionObject()

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
        calculationOption.setInterval(this, TimeHandler.instance.getIntervalFormat(this, timeRange, getString(R.string.format_time_api)))
        updateChart()
    }

    private fun updateChart() {
        BloodSugarService.instance.getRangesPercentage(calculationOption).doOnSuccess {
            if (it.second.component2() == null && it.second.component1() != null) {
                val bloodSugarRanges: BloodSugarRangesPercentageObject = it.second.component1()!!
                PieChartHandler.handleBarChartStyle(averagePieChart, this)
                val points = mutableListOf(
                    Pair(bloodSugarRanges.in_target!!, ContextCompat.getColor(this, R.color.colorPrimary)),
                    Pair(bloodSugarRanges.hypoglycemia!!, ContextCompat.getColor(this, R.color.colorAccent)),
                    Pair(bloodSugarRanges.hyperglycemia!!, ContextCompat.getColor(this, R.color.colorBackgroundDarkGray)))
                PieChartHandler.updateChartData(this, averagePieChart, points.toTypedArray())
                hypoglycemia_target_text.text = bloodSugarRanges.hypoglycemia.toString();
                hyperglycemia_target_text.text = bloodSugarRanges.hyperglycemia.toString();
                in_target_text.text = bloodSugarRanges.in_target.toString();
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
        }.subscribe()
    }
}
