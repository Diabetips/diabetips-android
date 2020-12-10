package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
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
            barChart.setCenterTextSize(50f);

        }

        fun updateChartData(context: Context, pieChart: PieChart, items: Array<Pair<Float, Int>>) {

            val yvalues: MutableList<PieEntry> = ArrayList()
            items.forEach {
                yvalues.add(PieEntry(it.first))
            }

            val dataSet = PieDataSet(yvalues, "Percentages")
            dataSet.valueTextSize = 40f
            dataSet.valueTypeface = ResourcesCompat.getFont(context, R.font.muli_bold)
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueFormatter = RoundValueFormatter()
            dataSet.resetColors();
            for (item in items) {
                dataSet.addColor(item.second)
            }

            val data = PieData(dataSet)

            data.setValueFormatter(PercentFormatter())

            pieChart.data = data;
            pieChart.animateY(800)
            // refresh the drawing
            pieChart.invalidate()
        }

    }
}