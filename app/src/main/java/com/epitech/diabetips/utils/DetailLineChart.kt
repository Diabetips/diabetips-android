package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.github.mikephil.charting.charts.LineChart

class DetailLineChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LineChart(context, attrs, defStyle) {
    private var mYAxisLimitZonePaint: Paint = Paint()

    init {
        super.init()
        mYAxisLimitZonePaint.style = Paint.Style.FILL
        mYAxisLimitZonePaint.color = Color.TRANSPARENT
    }

    override fun onDraw(canvas: Canvas) {
        val limitLines = mAxisLeft.limitLines
        if (limitLines == null || limitLines.size != 2) return
        val pts = FloatArray(4)
        val l1 = limitLines[0]
        val l2 = limitLines[1]
        pts[1] = l1.limit
        pts[3] = l2.limit
        mLeftAxisTransformer.pointValuesToPixel(pts)
        canvas.drawRect(mViewPortHandler.contentLeft(), pts[1], mViewPortHandler.contentRight(), pts[3], mYAxisLimitZonePaint)
        super.onDraw(canvas)
    }

    fun setLimitZoneColor(color: Int) {
        mYAxisLimitZonePaint.color = color
    }
}