package com.epitech.diabetips.services

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.FloatPoint
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.utils.TimeHandler
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import io.reactivex.Single
import kotlin.random.Random

class FakeDayDataGenerator(private val injectionInterval: Int, private val startValue: Float = 100f, private val random: Random = Random(1)) {
    private val path = Path()
    private val points = mutableListOf<MyPoint>()
    private val conPoint1 = mutableListOf<PointF>()
    private val conPoint2 = mutableListOf<PointF>()
    private val granularity = 150;
    private val spikeGenerator = SpikeGenerator();
    private var lastValue: Float = 100f

    private val nbInjectionsInOneDay: Int = 24 * (60 / this.injectionInterval);

    fun getDay(): Array<FloatPoint?> {
        calculatePointsForData()
        calculateConnectionPointsForBezierCurve()
        generateBezierCurve()
        return toFixedPoints(getPoints(), 24);
    }

    private fun getPoints(): List<FloatPoint?>? {
        val pointArray: Array<FloatPoint?> = arrayOfNulls<FloatPoint>(granularity)
        val pm = PathMeasure(path, false)
        val length = pm.length
        var distance = 0f
        val speed = length / (granularity)
        var counter = 0
        val aCoordinates = FloatArray(2)
        while (distance < length && counter < (granularity)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = FloatPoint(
                aCoordinates[0],
                aCoordinates[1])
            counter++
            distance = distance + speed
        }
        return pointArray.sortedWith(compareBy { it?.x })
    }

    private fun toFixedPoints(points: List<FloatPoint?>?, nbHours: Int): Array<FloatPoint?> {
        val size = (nbHours * (60 / injectionInterval));
        val fixedPointArray: Array<FloatPoint?> = arrayOfNulls<FloatPoint>(size)
        var lastIndex = 0;
        for (i in 0 until size) {
            val x: Float = (i * nbHours).toFloat() / size.toFloat();

            while (lastIndex < granularity && points!![lastIndex]!!.x < x) {
                lastIndex += 1;
            }
            if (lastIndex >= granularity)
                fixedPointArray[i] = FloatPoint(x, points!![granularity - 1]!!.y);
            else if (lastIndex == granularity - 1 || points!![lastIndex]!!.x == x) {
                fixedPointArray[i] = FloatPoint(x, points!![lastIndex]!!.y);
            } else {
                val y1 = points[lastIndex - 1]!!.y;
                val y2 = points[lastIndex]!!.y;
                val x1 = points[lastIndex - 1]!!.x;
                val x2 = points[lastIndex]!!.x;
                val y = (y2 - y1) * ((x - x1) / (x2 - x1)) + y1;
                fixedPointArray[i] = FloatPoint(x, y);
            }
        }
        //print(fixedPointArray.joinToString("\n") { it?.x.toString() + "," + it?.y.toString()})
        return fixedPointArray;
    }

    private fun calculatePointsForData() {
        addLimits(100f);
        addMealToCurve(9f, top = 150f);
        addMealToCurve(13f, top = 180f);
        addMealToCurve(18f, top = 170f);
        addHypo(15f, min=20f)
        addHypo(15.2f, min=30f)
    }

    private fun addLimits(base: Float = 100f)
    {
        points.add(MyPoint(0f, startValue))
        points.add(MyPoint(1f, startValue))
        lastValue = base + random.nextInt(-10, 10).toFloat()
        points.add(MyPoint(23f, lastValue))
        points.add(MyPoint(24f, lastValue))
        points.sortBy { x -> x.x }
    }

    private fun addMealToCurve(date: Float, base: Float = 100f, top: Float = 160f)
    {
        val times = arrayOf<Float>(date + (15f / 60f), date + (60f / 60f), date + (110f / 60f))
        spikeGenerator.addSpike(points, times,
            base + random.nextInt(-10, +10),
            top + random.nextInt(-30, +30))
    }

    private fun addHypo(date: Float, base: Float = 100f, min: Float = 40f)
    {
        val times = arrayOf<Float>(date, date + (10f / 60f), date + (110f / 60f))
        spikeGenerator.addSpike(points, times,
            base + random.nextInt(-10, 10),
            min + random.nextInt(-10, +10))
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

    private fun generateBezierCurve() {
        try {
            if (points.isEmpty() && conPoint1.isEmpty() && conPoint2.isEmpty()) return

            path.reset()
            path.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                //Log.d("GOOOO : ", points[i].x.toString() + " : " + points[i].y.toString());
                path.cubicTo(
                    conPoint1[i - 1].x, conPoint1[i - 1].y, conPoint2[i - 1].x, conPoint2[i - 1].y,
                    points[i].x, points[i].y
                )
            }
        } catch (e: Exception) {
        }
    }
    public fun removeData(date: Long, timeFormat: String): Single<Pair<Response, Result<BloodSugarObject, FuelError>>> {
        val end = TimeHandler.instance.currentTime()
        val start = TimeHandler.instance.changeTimestampTime(date, 0, 0)
        return BloodSugarService.instance.deleteAllMeasures(
            TimeHandler.instance.formatTimestamp(start, timeFormat),
            TimeHandler.instance.formatTimestamp(end, timeFormat))
    }


    public fun sendData(newPoints: Array<FloatPoint?>, date: Long, trimData: Boolean, timeFormat: String): Single<Pair<Response, Result<BloodSugarObject, FuelError>>> {
        val bs = BloodSugarObject();
        bs.start = TimeHandler.instance.formatTimestamp(date, timeFormat)
        if (trimData) {
            val elapsedTime = (TimeHandler.instance.currentTime() - (date - 3600 * 1000 * 5)) / 1000;
            bs.measures = newPoints.filter { it!!.x * 3600 <= elapsedTime }.map { it!!.y.toInt() }.toTypedArray()
        } else
            bs.measures = newPoints.map { it!!.y.toInt() }.toTypedArray()
        bs.interval = injectionInterval * 60;
        return BloodSugarService.instance.postMeasures(bs)
    }



    fun getLastValue(): Float {
        return lastValue;
    }
}

class MyPoint(x: Float, y: Float, val type: Type = Type.FLAT) : PointF(x, y)
{

    enum class Type {
        UP,
        DOWN,
        FLAT
    }
}

class SpikeGenerator()
{

    public fun addSpike(points: MutableList<MyPoint>, times: Array<Float>, side: Float, top: Float, type: MyPoint.Type = MyPoint.Type.FLAT)
    {
        if (!canBuild(points, times, type))
            return;

        val newPoints = mutableListOf<MyPoint>()
        newPoints.add(MyPoint(times[0], side, type))
        newPoints.add(MyPoint(times[1], top, type))
        newPoints.add(MyPoint(times[2], side, type))

        callibrate(points, newPoints)
    }

    private fun canBuild(points: MutableList<MyPoint>, times: Array<Float>, type: MyPoint.Type): Boolean {
        for (i in points.indices) {
            if (points[i].x > times[0] && points[i].x < times[2] && points[i].type != MyPoint.Type.FLAT && points[i].type != type)
                return false;
        }
        return true;
    }

    private fun callibrate(points: MutableList<MyPoint>, newPoints: MutableList<MyPoint>): Boolean {
        for (i in points.indices) {
            if (points[i].x > newPoints[0].x && points[i].x < newPoints[2].x) {
                if (Math.abs(points[i].x - newPoints[0].x) > Math.abs(newPoints[2].x - points[i].x))
                    newPoints[0].y = (newPoints[0].y + points[i].y) / 2
                else
                    newPoints[2].y = (newPoints[2].y + points[i].y) / 2
//                Log.d("Delete Point : ", points[i].x.toString() + " : " + points[i].y.toString())
                points.removeAt(i)
            }
        }
        for (point in newPoints) {
//            Log.d("Add Point : ", point.x.toString() + " : " + point.y.toString())
            points.add(point);
        }
        return true;
    }
}