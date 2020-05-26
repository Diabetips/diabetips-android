package com.epitech.diabetips.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.storages.EntryObject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
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

class ChartHandler {

    private object Holder { val INSTANCE = ChartHandler() }

    companion object {
        val instance: ChartHandler by lazy { Holder.INSTANCE }
    }

    fun handleLineChartStyle(lineChart: LineChart, context: Context) {
        lineChart.setViewPortOffsets(0f, 10f, 0f, 50f)
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisLeft.isEnabled = true
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisLeft.disableGridDashedLine()
        lineChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)

        lineChart.axisRight.isEnabled = false

        lineChart.xAxis.disableGridDashedLine()
        lineChart.setDrawGridBackground(false)
        lineChart.xAxis.isEnabled = true
        lineChart.xAxis.setDrawAxisLine(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.gridColor = ContextCompat.getColor(context, R.color.colorHint)
        lineChart.axisLeft.setDrawZeroLine(false)
        lineChart.setDrawMarkers(true)
        lineChart.setDrawBorders(false)
    }

    fun updateChartData(items: List<EntryObject>, intervalTimeStamp: Pair<Long, Long>, lineChart: LineChart, context: Context) {
        val formatter = HoursFormatter(intervalTimeStamp, context);
        lineChart.xAxis.valueFormatter = formatter

        val lineData = LineData()

        val bloodValues: List<BloodSugarObject> = items.filter{ it.type == EntryObject.Type.SUGAR}
            .map{it.orignal as (BloodSugarObject)}
        val bloodValuesChunks = cutBloodValuesIntoChunks(bloodValues, 1800f)
        for (bloodValueChunk in bloodValuesChunks) {
            lineData.addDataSet(generateBloodDataset(bloodValueChunk, intervalTimeStamp, context))
        }

        if (!items.isEmpty())
            drawLimits(lineData)

        val punctualInfo = mapOf(
            EntryObject.Type.MEAL to ContextCompat.getColor(context, R.color.colorPrimary),
            EntryObject.Type.INSULIN_FAST to ContextCompat.getColor(context, R.color.colorAccent),
            EntryObject.Type.INSULIN_SLOW to ContextCompat.getColor(context, R.color.colorAccent),
            EntryObject.Type.COMMENT to ContextCompat.getColor(context, R.color.colorBackgroundDarkLight))
        for (info in punctualInfo) {
            val filteredItems: List<EntryObject> = items.filter{ it.type == info.key}
            lineData.addDataSet(generatePonctualDataset(filteredItems, intervalTimeStamp, info.value))
        }

        lineChart.data = lineData
        lineChart.animateY(800)
        // refresh the drawing
        lineChart.invalidate()
    }

    private fun drawLimits(lineData: LineData) {
        val d = LineDataSet(listOf(Entry(0f,0f)), "ShowBottom")
        d.color = Color.TRANSPARENT
        d.setDrawCircles(false)
        d.setDrawValues(false)
        d.setDrawFilled(false)
        d.setDrawVerticalHighlightIndicator(false)
        d.setDrawHorizontalHighlightIndicator(false)
        lineData.addDataSet(d)
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

    fun generatePonctualDataset(items: List<EntryObject>, intervalTimeStamp: Pair<Long, Long>, color: Int): LineDataSet? {
        if (items.isEmpty())
            return null
        val yValues = mutableListOf<Entry>()
        for (item in items) {
            yValues.add(Entry((item.time - intervalTimeStamp.first).toFloat(), 100f))
        }
        val set = LineDataSet(yValues, items[0].type.toString())
        setPonctualElementDatasetStyle(set, color)
        return set
    }

    fun generateBloodDataset(bloodValues: List<BloodSugarObject>, intervalTimeStamp: Pair<Long, Long>, context: Context): LineDataSet{
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

    fun setPonctualElementDatasetStyle(dataSet: LineDataSet, color: Int) {
        dataSet.color = Color.TRANSPARENT
        dataSet.mode = LineDataSet.Mode.LINEAR
        dataSet.lineWidth = 0f
        dataSet.setDrawCircles(true)
        dataSet.setCircleColor(color)
        dataSet.circleHoleColor = color
        dataSet.circleRadius = 6f
        dataSet.circleHoleRadius = 6f
        dataSet.setDrawCircleHole(true)
        dataSet.setDrawValues(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawFilled(false)
        dataSet.isHighlightEnabled = false
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