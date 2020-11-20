package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData

class DetailPieChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : PieChart(context, attrs, defStyle) {
    init {
        super.init()
        setNoDataText("")
        setNoDataTextColor(MaterialHandler.getColorFromAttribute(context, R.attr.colorBackgroundText))
    }

    override fun setData(data: PieData?) {
        setData(data, R.string.no_data)
    }

    fun setData(data: PieData?, NoDataTextId: Int) {
        if ((data?.entryCount ?:0) > 0) {
            super.setData(data)
        } else {
            super.setData(null)
            setNoDataText(context.getString(NoDataTextId))
        }
    }
}