package com.epitech.diabetips.activities

import android.graphics.*
import android.os.Bundle
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.ADiabetipsActivity
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
        var p = getPoints();
        print(p?.joinToString("\n") { it?.x.toString()})
        print(p?.joinToString("\n") { it?.y.toString()})
    }

    private fun getPoints(): Array<FloatPoint?>? {
        val pointArray: Array<FloatPoint?> = arrayOfNulls<FloatPoint>(20)
        val pm = PathMeasure(path, false)
        val length = pm.length
        var distance = 0f
        val speed = length / 20
        var counter = 0
        val aCoordinates = FloatArray(2)
        while (distance < length && counter < 20) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = FloatPoint(
                aCoordinates[0],
                aCoordinates[1])
            counter++
            distance = distance + speed
        }
        return pointArray
    }

    private fun calculatePointsForData() {
        points.add(PointF(0f, 1f))
        points.add(PointF(1f, 2f))
        points.add(PointF(2f, 4f))
        points.add(PointF(3f, 3f))
        points.add(PointF(4f, 1f))
        points.add(PointF(5f, 2f))
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