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
import com.github.mikephil.charting.formatter.ValueFormatter
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
        val formatter = HoursFormatter(intervalTimeStamp, context);
        lineChart.xAxis.valueFormatter = formatter

        val bloodValuesChunks = cutBloodValuesIntoChunks(bloodValues, 1800f)
        val lineData = LineData()
        for (bloodValueChunk in bloodValuesChunks) {
            lineData.addDataSet(generateDataset(bloodValueChunk, intervalTimeStamp, context))
        }

        lineChart.data = lineData
        lineChart.animateY(800)
        // refresh the drawing
        lineChart.invalidate()
    }

    private fun cutBloodValuesIntoChunks(bloodValues: List<BloodSugarObject>, limit: Float): List<List<BloodSugarObject>> {
        val chunks = mutableListOf<List<BloodSugarObject>>()
        if (bloodValues.isEmpty())
            return chunks
        var lastValue: BloodSugarObject = bloodValues[0]
        var lastChunkIndex: Int = 0
        for ((index, value) in bloodValues.withIndex().drop(1)) {
            if (value.timestamp - lastValue.timestamp > limit) {
                chunks.add(bloodValues.subList(lastChunkIndex, index - 1))
                lastChunkIndex = index
            }
            lastValue = value
        }
        chunks.add(bloodValues.takeLast(bloodValues.size - lastChunkIndex))
        return chunks;
    }

    fun generateDataset(bloodValues: List<BloodSugarObject>, intervalTimeStamp: Pair<Long, Long>, context: Context): LineDataSet{
        val yValues = mutableListOf<Entry>()
        for (bloodValue in bloodValues) {
            yValues.add(Entry((bloodValue.timestamp - intervalTimeStamp.first).toFloat(), bloodValue.value.toFloat()))
        }
        val set = LineDataSet(yValues, "Glucose")
        setGlucoseDatasetStyle(set, context)
        return set
    }

    fun setGlucoseDatasetStyle(dataSet: LineDataSet, context: Context) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        dataSet.color = typedValue.data
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 1f
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
}

class HoursFormatter(private val intervalTimeStamp: Pair<Long, Long>, context: Context) : ValueFormatter() {

    public var labels = mutableMapOf<Float, String>()
    private var lastTimeValue = (intervalTimeStamp.second - intervalTimeStamp.first).toFloat()

    init {
        val localDateTimes = getDatesBetween(LocalDateTime.ofInstant(
            Instant.ofEpochSecond(intervalTimeStamp.first),
            ZoneOffset.UTC), LocalDateTime.ofInstant(
            Instant.ofEpochSecond(intervalTimeStamp.second),
            ZoneOffset.UTC))
        if (localDateTimes != null) {
            for (time in localDateTimes) {
                val timestamp = time!!.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000
                val index: Float = (timestamp - intervalTimeStamp.first).toFloat()
                val label: String = TimeHandler.instance.formatTimestamp(timestamp, context.getString(R.string.format_hour_24))
                labels.put(index, label);
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
        var chosen = "";
        for (item in labels) {
            val diff = Math.abs(item.key - value)
            if (diff < min) {
                chosen = item.value
                min = diff
            }
        }
        return chosen
    }

    fun getDatesBetween(
        startDate: LocalDateTime, endDate: LocalDateTime
    ): List<LocalDateTime?>? {

        val numOfHoursBetween: Long = ChronoUnit.HOURS.between(startDate, endDate)
        var date = startDate.minusMinutes(startDate.minute.toLong());
        date = date.minusSeconds(startDate.second.toLong());
        return MutableList(numOfHoursBetween.toInt()) {index -> index}.map{date.plusHours(it.toLong() + 1)}
    }
}