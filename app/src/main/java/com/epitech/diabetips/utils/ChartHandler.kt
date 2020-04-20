package com.epitech.diabetips.utils

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

    public fun updateChartData(weeklyBglMap: List<Pair<String, Int>>, lineChart: LineChart, context: Context) {
        val yValues = mutableListOf<Entry>()
        val xValues = mutableListOf<String>()
        var i = 0f
        for ((time, bgl) in weeklyBglMap) {
            //  create list of bgl values for yAxis
            yValues.add(Entry(i, bgl.toFloat()))
            xValues.add(time)
            i++
        }

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)

        val set1 = LineDataSet(yValues, "Bgl")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.lineWidth = 2f
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
}