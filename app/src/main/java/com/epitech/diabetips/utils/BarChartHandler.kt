package com.epitech.diabetips.utils

import android.content.Context
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.UserManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.abs
import kotlin.math.roundToInt

class BarChartHandler {

    companion object {

        fun handleBarChartStyle(barChart: BarChart, context: Context, ymax: Float = -1f) {
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.axisLeft.isEnabled = false
            barChart.axisRight.isEnabled = false
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.setDrawAxisLine(false)
            barChart.setDrawGridBackground(false)
            barChart.xAxis.isEnabled = true
            barChart.xAxis.setDrawAxisLine(false)
            barChart.xAxis.labelCount = 9
            barChart.xAxis.setAvoidFirstLastClipping(true)
            barChart.xAxis.axisMinimum = 0f
            barChart.xAxis.axisMaximum = 24f
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.xAxis.gridColor = ContextCompat.getColor(context, R.color.colorHint)
            barChart.xAxis.textColor = MaterialHandler.getColorFromAttribute(context, R.attr.colorHint)
            barChart.setDrawMarkers(true)
            barChart.setDrawBorders(false)
            barChart.setDrawBarShadow(true)
            barChart.setScaleEnabled(false)
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.axisMinimum = 0f
            if (ymax > 0) {
                barChart.axisLeft.axisMaximum = ymax
                barChart.axisRight.axisMaximum = ymax
            }
        }

        fun updateChartData(context: Context, items: Array<Pair<Float, Float>>, barWidth: Float, barChart: DetailBarChart, noDataTextId: Int = R.string.no_data) {
            val formatter = FloatToHourFormatter(context)
            barChart.xAxis.valueFormatter = formatter
            val barList: MutableList<IBarDataSet> = ArrayList()
            val biometrics = UserManager.instance.getBiometric(context)

            //Add Data
            items.forEach { item ->
                if (item.second != 0f) {
                    barList.add(
                        generateBarDataset(
                            context, item.first, item.second,
                            if (item.second < (biometrics.hypoglycemia ?: 0) || item.second > (biometrics.hyperglycemia ?: Int.MAX_VALUE))
                                R.color.colorAccent
                            else
                                R.color.colorPrimary
                        )
                    )
                }
            }
            val barData = BarData(barList)
            barData.isHighlightEnabled = false
            if (barData.yMax > 0) {
                barData.barWidth = barWidth
                barChart.data = barData
            } else {
                barChart.setData(null, noDataTextId)
            }
            if (barData.yMax > barChart.axisLeft.axisMaximum) {
                barChart.axisLeft.axisMaximum = barData.yMax
                barChart.axisRight.axisMaximum = barData.yMax
            }
            barChart.animateY(800)
            // refresh the drawing
            barChart.invalidate()
        }

        private fun generateBarDataset(context: Context, x: Float, y: Float, color: Int): BarDataSet {
            val barDataset = BarDataSet(arrayListOf(BarEntry(x, y)), "")
            barDataset.color = ContextCompat.getColor(context, color)
            barDataset.valueTextSize = context.resources.getDimension(R.dimen.chart_value_size)
            barDataset.valueFormatter = RoundValueFormatter()
            return barDataset
        }
    }
}

class FloatToHourFormatter(context: Context) : ValueFormatter() {

    private val is24Format: Boolean = DateFormat.is24HourFormat(context)

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return if (is24Format) {
            if (abs(value) % 1f < 0.01) {
                "${value.toInt()}H"
            } else {
                "${value.toInt()}H${((value % 1) * 60).roundToInt()}"
            }
        } else {
            if (abs(value) % 1f < 0.01) {
                "${if (value.toInt() % 12 == 0) 12 else value.toInt() % 12}"
            } else {
                "${if (value.toInt() % 12 == 0) 12 else value.toInt() % 12}:${((value % 1) * 60).roundToInt()}"
            } + if (value >= 12) " AM" else " PM"
        }
    }

}

class RoundValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.roundToInt().toString()
    }

}