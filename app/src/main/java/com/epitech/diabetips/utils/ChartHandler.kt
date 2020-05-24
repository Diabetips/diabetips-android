package com.epitech.diabetips.utils

import android.content.Context
import android.util.Log
import android.util.TypedValue
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.BloodSugarObject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit


class ChartHandler {

    private object Holder { val INSTANCE = ChartHandler() }

    companion object {
        val instance: ChartHandler by lazy { Holder.INSTANCE }
    }

    fun handleLineChartStyle(lineChart: LineChart) {
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisLeft.isEnabled = true
        lineChart.axisLeft.disableGridDashedLine()
        lineChart.xAxis.disableGridDashedLine()
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.isEnabled = true
        lineChart.setDrawGridBackground(false)
        lineChart.axisLeft.setDrawZeroLine(true)
        lineChart.setDrawMarkers(false)
        lineChart.setDrawBorders(false)
    }

    public fun updateChartData(bloodValues: List<BloodSugarObject>, intervalTimeStamp: Pair<Long, Long>, lineChart: LineChart, context: Context) {
        Log.d("Chart", "Update")
        val yValues = mutableListOf<Entry>()
        val xValues = mutableListOf<String>()
        for (bloodValue in bloodValues) {
            //  create list of bgl values for yAxis
            yValues.add(Entry((bloodValue.timestamp - intervalTimeStamp.first).toFloat(), bloodValue.value.toFloat()))
        }
        val inter = getDatesBetween(LocalDateTime.ofInstant(
            Instant.ofEpochSecond(intervalTimeStamp.first),
            ZoneOffset.UTC), LocalDateTime.ofInstant(
            Instant.ofEpochSecond(intervalTimeStamp.second),
            ZoneOffset.UTC))
        lineChart.xAxis.axisMinimum = 0f;
        lineChart.xAxis.axisMaximum = (intervalTimeStamp.second - intervalTimeStamp.first).toFloat()
//
//        val xAxisLabel: ArrayList<String> = ArrayList(initialCapacity = (intervalTimeStamp.second - intervalTimeStamp.first).toInt())
////        xAxisLabel.addAll()
//        inter!!.forEach {
//            run {
//                val timestamp = it!!.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000
//                val index: Int = (timestamp - intervalTimeStamp.first).toInt()
//                val label: String = TimeHandler.instance.formatTimestamp(timestamp, "hh:mm")
//                Log.d("Add time : ", label)
//                xAxisLabel.add(index, label)
//            }
//        }
//        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)

        val set1 = LineDataSet(yValues, "Bgl")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.lineWidth = 1f
        set1.fillAlpha = 50
        set1.setDrawVerticalHighlightIndicator(true)
        set1.setDrawHorizontalHighlightIndicator(true)

        customiseDataSet(set1, context)
        val dataSets = listOf<LineDataSet>(set1)
        val lineData = LineData(dataSets)
        lineData.setValueTextSize(9f)
        lineData.setDrawValues(false)
        lineData.isHighlightEnabled = true

        lineChart.data = lineData

        lineChart.animateY(800)
        // refresh the drawing
        lineChart.invalidate()
    }

    fun handleLineDataCreation(context: Context, entries: ArrayList<Entry>) : LineData {
        val typedValue = TypedValue()

        val dataSet: LineDataSet = LineDataSet(entries, "sugar")
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        dataSet.color = typedValue.data
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        val lineData: LineData = LineData(dataSet)
        lineData.setDrawValues(false)
        return lineData
    }

    fun customiseDataSet(dataSet: LineDataSet, context: Context) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        dataSet.color = typedValue.data
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
    }

    fun updateChartData(bloodValues: List<BloodSugarObject>, intervalTimeStamp: Long, lineChart: Long) {

    }

    fun getDatesBetween(
        startDate: LocalDateTime, endDate: LocalDateTime
    ): List<LocalDateTime?>? {

        val numOfHoursBetween: Long = ChronoUnit.HOURS.between(startDate, endDate)
        startDate.minusMinutes(startDate.minute.toLong());
        startDate.minusSeconds(startDate.second.toLong());
        return MutableList(numOfHoursBetween.toInt()) {index -> index}.map{startDate.plusHours(it.toLong() + 1)}
    }
}