package com.epitech.diabetips.utils

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.*

class ChartHandler {

    private object Holder { val INSTANCE = ChartHandler() }

    companion object {
        val instance: ChartHandler by lazy { Holder.INSTANCE }
    }

    fun handleLineChartStyle(lineChart: LineChart) {
        lineChart.setDrawGridBackground(false)
        lineChart.setDrawBorders(false)
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.isEnabled = false
        lineChart.setDrawMarkers(false)
        lineChart.setDrawBorders(false)
    }

    fun handleLineDataCreation(context: Context, entries: ArrayList<Entry>) : LineData {
        val typedValue = TypedValue()
        val dataSet: LineDataSet = LineDataSet(entries, "")
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        dataSet.color = typedValue.data
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        val lineData: LineData = LineData(dataSet)
        lineData.setDrawValues(false)
        return lineData
    }
}