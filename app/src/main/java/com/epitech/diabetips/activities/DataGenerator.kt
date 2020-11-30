package com.epitech.diabetips.activities

import android.graphics.*
import android.os.Bundle
import android.util.Log
import com.epitech.diabetips.R
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_data_generator.*
import kotlin.random.Random


class DataGenerator : ADiabetipsActivity(R.layout.activity_data_generator) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateButton.setOnClickListener{generate()}
        bitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        viewCanvas = Canvas(bitmap!!)
    }

    private val path = Path()
    private val borderPath = Path()
    private val borderPathPaint = Paint()

    private val points = mutableListOf<PointF>()
    private val conPoint1 = mutableListOf<PointF>()
    private val conPoint2 = mutableListOf<PointF>()

    private var viewCanvas: Canvas? = null
    private var bitmap: Bitmap? = null
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)

    private fun generate()
    {
        calculatePointsForData()
        calculateConnectionPointsForBezierCurve()
        bitmap = Bitmap.createBitmap(graph.width, graph.height, Bitmap.Config.ARGB_8888)
        viewCanvas = Canvas(bitmap!!)
        drawBezierCurve(viewCanvas)
        graph.setImageBitmap(bitmap);
        val p = getPoints();
        SendData(p);
        print(p?.joinToString("\n") { it?.x.toString() + "," + it?.y.toString()})
    }

    private fun SendData(points: List<FloatPoint?>?)
    {
        val bs = BloodSugarObject();
        bs.start = TimeHandler.instance.formatTimestamp(TimeHandler.instance.trimTimestamp(TimeHandler.instance.currentTime()), getString(R.string.format_time_api))
        bs.measures = points?.map { it!!.y.toInt() }!!.toTypedArray()
        bs.interval = 15 * 60;
        BloodSugarService.instance.postMeasures(bs).doOnSuccess {
            if (it.second.component2() == null) {
                print(it.second.component1()!!.time);
            }
        }.subscribe()
    }

    private fun getPoints(): List<FloatPoint?>? {
        val pointArray: Array<FloatPoint?> = arrayOfNulls<FloatPoint>(24 * 4)
        val pm = PathMeasure(path, false)
        val length = pm.length
        var distance = 0f
        val speed = length / (24 * 4)
        var counter = 0
        val aCoordinates = FloatArray(2)
        while (distance < length && counter < (24 * 4)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = FloatPoint(
                aCoordinates[0],
                aCoordinates[1])
            counter++
            distance = distance + speed
        }
        return pointArray?.sortedWith(compareBy { it?.x })
    }

    private fun calculatePointsForData() {
        addLimits(100f);
        addMealToCurve(9f, max = 150f);
        addMealToCurve(13f, max = 180f);
        addMealToCurve(18f, max = 170f);
    }

    private fun addLimits(base: Float = 100f)
    {
        var newBase = base + Random.nextInt(-10, 10).toFloat()
        points.add(PointF(0f, newBase))
        points.add(PointF(1f, newBase))
        newBase = base + Random.nextInt(-10, 10).toFloat()
        points.add(PointF(23f, newBase))
        points.add(PointF(24f, newBase))
    }

    private fun addMealToCurve(date: Float, base: Float = 100f, max: Float = 160f)
    {
        points.add(PointF(date + (15f / 60f), base + Random.nextInt(-10, 10)))
        points.add(PointF(date + (60f / 60f), max + Random.nextInt(-20, 30)))
        points.add(PointF(date + (110f / 60f), base + Random.nextInt(-10, 10)))
    }

    private fun addHypo(date: Float, base: Float = 100f, max: Float = 160f)
    {
        points.add(PointF(date + (15f / 60f), base + Random.nextInt(-10, 10)))
        points.add(PointF(date + (60f / 60f), max + Random.nextInt(-20, 30)))
        points.add(PointF(date + (110f / 60f), base + Random.nextInt(-10, 10)))
    }

    private fun calculateConnectionPointsForBezierCurve() {
        try {
            for (i in 1 until points.size) {
                conPoint1.add(PointF((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
                conPoint2.add(PointF((points[i].x + points[i - 1].x) / 2, points[i].y))
            }
        } catch (e: Exception) {
        }
    }

    private fun drawBezierCurve(canvas: Canvas?) {

        try {

            if (points.isEmpty() && conPoint1.isEmpty() && conPoint2.isEmpty()) return

            path.reset()
            path.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                path.cubicTo(
                    conPoint1[i - 1].x, conPoint1[i - 1].y, conPoint2[i - 1].x, conPoint2[i - 1].y,
                    points[i].x, points[i].y
                )
            }
            borderPathPaint.apply {
                isAntiAlias = true
                strokeWidth = 8f
                style = Paint.Style.STROKE
                color = Color.parseColor("#FFFC4F47")
            }

            borderPath.set(path)

            canvas?.drawPath(borderPath, borderPathPaint)
        } catch (e: Exception) {
        }
    }
}

internal class FloatPoint(var x: Float, var y: Float)