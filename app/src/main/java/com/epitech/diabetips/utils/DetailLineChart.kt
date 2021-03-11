package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData

class DetailLineChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LineChart(context, attrs, defStyle) {
    private var mYAxisLimitZonePaint: Paint = Paint()
    private val points = FloatArray(4)

    init {
        super.init()
        mYAxisLimitZonePaint.style = Paint.Style.FILL
        mYAxisLimitZonePaint.color = Color.TRANSPARENT
        setNoDataText("")
        setNoDataTextColor(MaterialHandler.getColorFromAttribute(context, R.attr.colorBackgroundText))
    }

    override fun onDraw(canvas: Canvas) {
        val limitLines = mAxisLeft.limitLines
        if (limitLines != null && limitLines.size == 2) {
            val l1 = limitLines[0]
            val l2 = limitLines[1]
            points[1] = l1.limit
            points[3] = l2.limit
            mLeftAxisTransformer.pointValuesToPixel(points)
            canvas.drawRect(mViewPortHandler.contentLeft(), points[1], mViewPortHandler.contentRight(), points[3], mYAxisLimitZonePaint)
        }
        super.onDraw(canvas)
    }

    fun setLimitZoneColor(color: Int) {
        mYAxisLimitZonePaint.color = color
    }

    override fun setData(data: LineData?) {
        setData(data, R.string.no_data)
    }

    fun setData(data: LineData?, NoDataTextId: Int) {
        if ((data?.entryCount ?:0) > 0) {
            super.setData(data)
        } else {
            super.setData(null)
            setNoDataText(context.getString(NoDataTextId))
        }
    }
}