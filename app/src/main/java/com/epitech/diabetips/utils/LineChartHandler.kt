package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.storages.EntryObject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs

class ChartHandler {

    companion object {

        fun handleLineChartStyle(lineChart: LineChart, context: Context) {
            lineChart.setViewPortOffsets(0f, 10f, 0f, 50f)
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.axisLeft.isEnabled = true
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisLeft.setDrawAxisLine(false)
            lineChart.axisLeft.disableGridDashedLine()
            lineChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            lineChart.axisLeft.setDrawLimitLinesBehindData(true)
            lineChart.axisLeft.textColor = MaterialHandler.getColorFromAttribute(context, R.attr.colorBackgroundText)
            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.disableGridDashedLine()
            lineChart.setDrawGridBackground(false)
            lineChart.xAxis.isEnabled = true
            lineChart.xAxis.setDrawAxisLine(false)
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.xAxis.gridColor = ContextCompat.getColor(context, R.color.colorHint)
            lineChart.xAxis.textColor = MaterialHandler.getColorFromAttribute(context, R.attr.colorBackgroundText)
            lineChart.axisLeft.setDrawZeroLine(false)
            lineChart.setDrawMarkers(true)
            lineChart.setDrawBorders(false)
        }

        fun updateChartData(items: List<EntryObject>, intervalTimeStamp: Pair<Long, Long>, lineChart: DetailLineChart, context: Context, noDataTextId: Int = R.string.no_data) {
            val formatter = HoursFormatter(intervalTimeStamp, context)
            lineChart.xAxis.valueFormatter = formatter

            val lineData = LineData()

            val bloodValues: List<BloodSugarObject> = items.filter { it.type == ObjectType.SUGAR }
                .map { it.orignal as (BloodSugarObject) }
            val bloodValuesChunks = cutBloodValuesIntoChunks(bloodValues, 1800f, context)
            for (bloodValueChunk in bloodValuesChunks) {
                lineData.addDataSet(generateBloodDataset(bloodValueChunk, intervalTimeStamp, context))
            }

            if (items.isNotEmpty()) {
                drawLimit(lineData, 0f)
                drawLimit(lineData, 250f)
            }

            val punctualInfo = mapOf(
                ObjectType.INSULIN_FAST to ContextCompat.getDrawable(context, R.drawable.ic_syringe).apply {
                    this?.setTint(ContextCompat.getColor(context, R.color.colorAccent))
                },
                ObjectType.INSULIN_SLOW to ContextCompat.getDrawable(context, R.drawable.ic_syringe_alt).apply {
                    this?.setTint(ContextCompat.getColor(context, R.color.colorAccentLight))
                },
                ObjectType.MEAL to ContextCompat.getDrawable(context, R.drawable.ic_fork).apply {
                    this?.setTint(ContextCompat.getColor(context, R.color.colorPrimary))
                },
                ObjectType.ACTIVITY to ContextCompat.getDrawable(context, R.drawable.ic_activity).apply {
                    this?.setTint(ContextCompat.getColor(context, R.color.colorGreen))
                },
                ObjectType.NOTE to ContextCompat.getDrawable(context, R.drawable.ic_comment).apply {
                    this?.setTint(MaterialHandler.getColorFromAttribute(context, R.attr.colorComment))
                })
            for (info in punctualInfo) {
                val filteredItems: List<EntryObject> = items.filter { it.type == info.key }
                lineData.addDataSet(generatePonctualDataset(filteredItems, intervalTimeStamp, info.value, context))
            }
            lineChart.axisLeft.removeAllLimitLines()
            val biometrics = UserManager.instance.getBiometric(context)
            if (biometrics.hypoglycemia != null && biometrics.hyperglycemia != null) {
                val limitColor = ContextCompat.getColor(context, R.color.colorPrimaryLight)
                lineChart.setLimitZoneColor(limitColor)
                lineChart.axisLeft.addLimitLine(generateLimitLine(biometrics.hypoglycemia!!.toFloat(), limitColor))
                lineChart.axisLeft.addLimitLine(generateLimitLine(biometrics.hyperglycemia!!.toFloat(), limitColor))
            }
            if (lineData.entryCount > 0) {
                lineChart.data = lineData
            } else {
                lineChart.setData(null, noDataTextId)
            }
            lineChart.animateY(800)
            // refresh the drawing
            lineChart.invalidate()
        }

        private fun drawLimit(lineData: LineData, yPos: Float = 0f) {
            val d = LineDataSet(listOf(Entry(0f, yPos)), "ShowBottom")
            d.color = Color.TRANSPARENT
            d.setDrawCircles(false)
            d.setDrawValues(false)
            d.setDrawFilled(false)
            d.setDrawVerticalHighlightIndicator(false)
            d.setDrawHorizontalHighlightIndicator(false)
            lineData.addDataSet(d)
        }

        private fun cutBloodValuesIntoChunks(bloodValues: List<BloodSugarObject>, limit: Float, context: Context): List<List<BloodSugarObject>> {
            val chunks = mutableListOf<List<BloodSugarObject>>()
            if (bloodValues.isEmpty())
                return chunks
            var lastValue: BloodSugarObject = bloodValues[0]
            var lastChunkIndex = 0
            for ((index, value) in bloodValues.withIndex().drop(1)) {
                if (TimeHandler.instance.getSecondDiffFormat(value.time, lastValue.time, context.getString(R.string.format_time_api)) > limit) {
                    chunks.add(bloodValues.subList(lastChunkIndex, index - 1))
                    lastChunkIndex = index
                }
                lastValue = value
            }
            chunks.add(bloodValues.takeLast(bloodValues.size - lastChunkIndex))
            return chunks
        }

        private fun generatePonctualDataset(items: List<EntryObject>, intervalTimeStamp: Pair<Long, Long>, drawable: Drawable?, context: Context): LineDataSet? {
            if (items.isEmpty())
                return null
            val yValues = mutableListOf<Entry>()
            val bitmapDrawable = BitmapDrawable(context.resources, drawable?.toBitmap(42, 42))
            for (item in items) {
                yValues.add(Entry((((TimeHandler.instance.getTimestampFromFormat(item.time, context.getString(R.string.format_time_api)) ?: 0) - intervalTimeStamp.first).toFloat()), 225f, bitmapDrawable))
            }
            val set = LineDataSet(yValues, items[0].type.toString())
            setPonctualElementDatasetStyle(set)
            return set
        }

        private fun generateBloodDataset(bloodValues: List<BloodSugarObject>, intervalTimeStamp: Pair<Long, Long>, context: Context): LineDataSet {
            val yValues = mutableListOf<Entry>()
            for (bloodValue in bloodValues) {
                yValues.add(Entry(((TimeHandler.instance.getTimestampFromFormat(bloodValue.time, context.getString(R.string.format_time_api)) ?: 0) - intervalTimeStamp.first).toFloat(), bloodValue.value.toFloat()))
            }
            val set = LineDataSet(yValues, "Glucose")
            setGlucoseDatasetStyle(set, context)
            return set
        }

        private fun generateLimitLine(value: Float, color: Int, label: String = ""): LimitLine {
            return LimitLine(value, label).apply {
                lineColor = color
            }
        }

        private fun setGlucoseDatasetStyle(dataSet: LineDataSet, context: Context) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            dataSet.color = typedValue.data
            dataSet.mode = LineDataSet.Mode.LINEAR
            dataSet.lineWidth = 2f
            dataSet.setDrawCircles(false)
            dataSet.setDrawValues(false)
            dataSet.setDrawVerticalHighlightIndicator(true)
            dataSet.setDrawHorizontalHighlightIndicator(true)
            dataSet.setDrawFilled(true)
            dataSet.fillAlpha = 50
            dataSet.valueTextSize = 9f
            dataSet.isHighlightEnabled = true
            dataSet.setDrawValues(false)
        }

        private fun setPonctualElementDatasetStyle(dataSet: LineDataSet) {
            dataSet.color = Color.TRANSPARENT
            dataSet.mode = LineDataSet.Mode.LINEAR
            dataSet.lineWidth = 0f
            dataSet.setDrawCircles(true)
            dataSet.setCircleColor(Color.TRANSPARENT)
            dataSet.circleHoleColor = Color.TRANSPARENT
            dataSet.circleRadius = 0f
            dataSet.circleHoleRadius = 0f
            dataSet.setDrawCircleHole(false)
            dataSet.setDrawValues(false)
            dataSet.setDrawVerticalHighlightIndicator(false)
            dataSet.setDrawHorizontalHighlightIndicator(false)
            dataSet.setDrawFilled(false)
            dataSet.isHighlightEnabled = false
            dataSet.setDrawValues(false)
        }
    }
}

class HoursFormatter(intervalTimeStamp: Pair<Long, Long>, context: Context) : ValueFormatter() {

    var labels = mutableMapOf<Float, String>()
    private var lastTimeValue = (intervalTimeStamp.second - intervalTimeStamp.first).toFloat()

    init {
        val localDateTimes = getDatesBetween(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(intervalTimeStamp.first),
            ZoneOffset.UTC), LocalDateTime.ofInstant(
            Instant.ofEpochMilli(intervalTimeStamp.second),
            ZoneOffset.UTC))
        if (localDateTimes != null) {
            for (time in localDateTimes) {
                val timestamp = time!!.toInstant(ZoneOffset.UTC).toEpochMilli()
                val index: Float = (timestamp - intervalTimeStamp.first).toFloat()
                val label: String = TimeHandler.instance.formatTimestamp(timestamp, context.getString(if (DateFormat.is24HourFormat(context)) R.string.format_hour_24 else R.string.format_hour_12))
                labels[index] = label
            }
        }
    }

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return findClosest(value)
    }

    fun findClosest(value: Float): String {
        var min = lastTimeValue
        var chosen = ""
        for (item in labels) {
            val diff = abs(item.key - value)
            if (diff < min) {
                chosen = item.value
                min = diff
            }
        }
        return chosen
    }

    fun getDatesBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<LocalDateTime?>? {
        val numOfHoursBetween: Long = ChronoUnit.HOURS.between(startDate, endDate)
        var date = startDate.minusMinutes(startDate.minute.toLong())
        date = date.minusSeconds(startDate.second.toLong())
        return MutableList(numOfHoursBetween.toInt()) {index -> index}.map{date.plusHours(it.toLong() + 1)}
    }
}