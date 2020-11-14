package com.epitech.diabetips.utils

import android.content.Context
import android.util.AttributeSet
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData

class DetailBarChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : BarChart(context, attrs, defStyle) {

    init {
        super.init()
        setNoDataText("")
        setNoDataTextColor(MaterialHandler.getColorFromAttribute(context, R.attr.colorBackgroundText))
        renderer = DetailBarChartRender(this, context.resources.getDimension(R.dimen.chart_corner_size))
    }

    override fun setData(data: BarData?) {
        setData(data, R.string.no_data)
    }

    fun setData(data: BarData?, NoDataTextId: Int) {
        if ((data?.entryCount ?:0) > 0) {
            super.setData(data)
        } else {
            super.setData(null)
            setNoDataText(context.getString(NoDataTextId))
        }
    }
}