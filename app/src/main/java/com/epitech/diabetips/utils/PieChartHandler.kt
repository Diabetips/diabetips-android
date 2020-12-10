package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter

class PieChartHandler {

    companion object {

        fun handleBarChartStyle(barChart: PieChart, context: Context) {
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.setDrawMarkers(true)
            barChart.isDrawHoleEnabled = false
            barChart.setDrawEntryLabels(true)
            barChart.setEntryLabelTextSize(50f)
            barChart.setEntryLabelColor(R.attr.colorBackground)
            barChart.setEntryLabelTypeface(ResourcesCompat.getFont(context, R.font.muli_bold))
            barChart.isHighlightPerTapEnabled = true
            barChart.setCenterTextSize(50f)
        }

        fun updateChartData(context: Context, pieChart: DetailPieChart, items: Array<Pair<Float, Int>>, noDataTextId: Int = R.string.no_data) {
            val yValues: MutableList<PieEntry> = ArrayList()
            items.forEach {
                yValues.add(PieEntry(it.first))
            }

            val dataSet = PieDataSet(yValues, "Percentages")
            dataSet.valueTextSize = 40f
            dataSet.valueTypeface = ResourcesCompat.getFont(context, R.font.muli_bold)
            dataSet.valueTextColor = Color.WHITE
            dataSet.resetColors()
            for (item in items) {
                dataSet.addColor(item.second)
            }

            val pieData = PieData(dataSet)
            if (pieData.yMax > 0) {
                pieData.setValueFormatter(RoundValueFormatter())
                pieChart.data = pieData
            } else {
                pieChart.setData(null, noDataTextId)
            }
            pieChart.animateY(800)
            // refresh the drawing
            pieChart.invalidate()
        }

    }
}