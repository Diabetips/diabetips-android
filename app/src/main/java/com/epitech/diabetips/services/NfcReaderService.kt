package com.epitech.diabetips.services

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcV
import android.os.AsyncTask
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.epitech.diabetips.freestylelibre.GlucoseData
import com.epitech.diabetips.freestylelibre.RawTagData
import com.epitech.diabetips.freestylelibre.SensorData
import com.epitech.diabetips.storages.BloodSugarObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and


var NFC_USE_MULTI_BLOCK_READ = true
var PENDING_INTENT_TECH_DISCOVERED = 1;

class NfcReaderService(var context: Context, myIntent: Intent, var activity: Activity) {

    val MIME_TEXT_PLAIN = "text/plain"

    private var mNfcAdapter: NfcAdapter? = null


    var lectura: String? = null
    private var buffer: kotlin.String? = null
    private var currentGlucose = 0f
    private val tvResult: TextView? = null
    private val last: TextView? = null
    var numHistoryValues = 32
    var historyIntervalInMinutes = 15
    var numTrendValues = 16
    public var minSensorAgeInMinutes: Long =
        60 // data generated by the sensor in the first 60 minutes is not correct

    private var data: ByteArray = ByteArray(360)
//    private val onDataReceived : ((RecipeObject) -> Unit)? = null


    init {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter == null) {
            throw IOException("This device doesn't support NFC.")
        }
        if (!mNfcAdapter!!.isEnabled) {
            throw IOException("NFC is disabled.")
        }
        handleIntent(myIntent)
        var glucoseDatas = ArrayList<GlucoseData>()
        try {
            val fos: FileOutputStream = context.applicationContext.openFileOutput(
                "glucose",
                Context.MODE_PRIVATE
            )
            fos.close()
            val fis: FileInputStream = context.applicationContext.openFileInput("glucose")
            val ois = ObjectInputStream(fis)
            glucoseDatas = ois.readObject() as ArrayList<GlucoseData>
            ois.close()
            fis.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } catch (c: ClassNotFoundException) {
            c.printStackTrace()
        }
        Log.d("Saved data", """$glucoseDatas""".trimIndent())
        if (glucoseDatas.size > 1)
            Log.d("Saved Sensor age", """${glucoseDatas[0].ageInSensorMinutes}""".trimIndent())
    }

    fun onResume() {
        setupForegroundDispatch(context as Activity, mNfcAdapter)
    }

    fun onPause() {
        stopForegroundDispatch(context as Activity, mNfcAdapter)
    }

    fun onNewIntent(intent: Intent?) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        Log.d("NfcReaderDiabetips", "NEW INTENT FOUND")
        if (intent != null) {
            handleIntent(intent)
        } else {
            Log.d("NfcReaderDiabetips", "No Intent found")
        }
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        Log.d("diabetips", "Action FOUND ! ! !")
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            Log.d("diabetips", "NfcAdapter.ACTION_TECH_DISCOVERED")
            // In case we would still use the Tech Discovered Intent
            val tag =
                intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val techList = tag.techList
            val searchedTech = NfcV::class.java.name
            NfcVReaderTask().execute(tag)
        }
    }

    /**
     * @param activity The corresponding [Activity] requesting the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    fun setupForegroundDispatch(activity: Activity, adapter: NfcAdapter?) {
//        val intent = Intent(activity.applicationContext, activity.javaClass)
//        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        val pendingIntent =
//            PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)
//        val filters = arrayOfNulls<IntentFilter>(1)
//        val techList =
//            arrayOf<Array<String>>()
//
//        // Notice that this is the same filter as in our manifest.
//        filters[0] = IntentFilter()
//        filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
//        filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
//        filters[0]!!.addDataType("*/*")
//        adapter!!.enableForegroundDispatch(activity, pendingIntent, filters, techList)
        val pi: PendingIntent = activity.createPendingResult(PENDING_INTENT_TECH_DISCOVERED, Intent(), 0)
        if (pi != null) {
            try {
                mNfcAdapter!!.enableForegroundDispatch(
                    activity,
                    pi,
                    arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)),
                    arrayOf(arrayOf("android.nfc.tech.NfcV"))
                )
            } catch (e: NullPointerException) {
                // Drop NullPointerException
            }
        }

        Log.d("TOTO", "SETUP ForegroundDispatch")
    }

    /**
     * @param activity The corresponding {BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    fun stopForegroundDispatch(activity: Activity?, adapter: NfcAdapter?) {
        adapter!!.disableForegroundDispatch(activity)
    }

    private val LOG_ID = "OpenLibre::" + NfcVReaderTask::class.java.simpleName
    private val nfcReadTimeout: Long = 1000 // [ms]

    private var sensorTagId: String? = null

//    private inner class NfcVReaderTask :
//        AsyncTask<Tag?, Void?, String?>() {
//        override fun onPostExecute(result: String?) {
//            val vibrator =
//                context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
//            vibrator!!.vibrate(1000)
//            //Abbott.this.finish();
//        }
//
//        override fun doInBackground(vararg params: Tag?): String? {
//            val tag = params[0]
//            val nfcvTag = NfcV.get(tag)
//            Log.d("Diabetips NfcReader", "Enter NdefReaderTask: $nfcvTag")
//            Log.d("Diabetips NfcReader", "Tag ID: " + tag!!.id)
//            try {
//                nfcvTag.connect()
//            } catch (e: IOException) {
//                activity.runOnUiThread(Runnable {
//                    Toast.makeText(
//                        activity.getApplicationContext(),
//                        "Error opening NFC connection!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
//                return null
//            }
//            lectura = ""
//            val bloques =
//                Array(40) { ByteArray(8) }
//            val allBlocks = ByteArray(40 * 8)
//            Log.d(
//                "Diabetips NfcReader",
//                "---------------------------------------------------------------"
//            )
//            //Log.d("Diabetips NfcReader", "nfcvTag ID: "+nfcvTag.getDsfId());
//
//            //Log.d("Diabetips NfcReader", "getMaxTransceiveLength: "+nfcvTag.getMaxTransceiveLength());
//            try {
//
//                // Get system information (0x2B)
//                var cmd = byteArrayOf(
//                    0x00.toByte(),  // Flags
//                    0x2B.toByte() // Command: Get system information
//                )
//                var systeminfo = nfcvTag.transceive(cmd)
//
//                //Log.d("Diabetips NfcReader", "systeminfo: "+systeminfo.toString()+" - "+systeminfo.length);
//                //Log.d("Diabetips NfcReader", "systeminfo HEX: "+bytesToHex(systeminfo));
//                systeminfo = Arrays.copyOfRange(systeminfo, 2, systeminfo.size - 1)
//                val memorySize = byteArrayOf(systeminfo[6], systeminfo[5])
//                Log.d(
//                    "Diabetips NfcReader",
//                    "Memory Size: " + bytesToHex(memorySize) + " / " + bytesToHex(memorySize).trim({ it <= ' ' })
//                        .toInt(16)
//                )
//                val blocks = byteArrayOf(systeminfo[8])
//                Log.d(
//                    "Diabetips NfcReader",
//                    "blocks: " + bytesToHex(blocks) + " / " + bytesToHex(blocks).trim({ it <= ' ' })
//                        .toInt(16)
//                )
//                val totalBlocks: Int = bytesToHex(blocks).trim({ it <= ' ' }).toInt(16)
//                for (i in 3..40) { // Leer solo los bloques que nos interesan
//                    /*
//	                cmd = new byte[] {
//	                    (byte)0x00, // Flags
//	                    (byte)0x23, // Command: Read multiple blocks
//	                    (byte)i, // First block (offset)
//	                    (byte)0x01  // Number of blocks
//	                };
//	                */
//                    // Read single block
//                    cmd = byteArrayOf(
//                        0x00.toByte(),  // Flags
//                        0x20.toByte(),  // Command: Read multiple blocks
//                        i.toByte() // block (offset)
//                    )
//                    var oneBlock = nfcvTag.transceive(cmd)
//                    Log.d(
//                        "Diabetips NfcReader",
//                        "userdata: " + oneBlock.toString() + " - " + oneBlock.size
//                    )
//                    oneBlock = Arrays.copyOfRange(oneBlock, 1, oneBlock.size)
//                    bloques[i - 3] = Arrays.copyOf(oneBlock, 8)
//                    Log.d("Diabetips NfcReader", "userdata HEX: " + bytesToHex(oneBlock))
//                    lectura = lectura + bytesToHex(oneBlock) + "\r\n"
//                }
//                var s = ""
//                for (i in 0..39) {
//                    Log.d("Diabetips NfcReader", bytesToHex(bloques[i]))
//                    s = s + bytesToHex(bloques[i])
//                }
//                Log.d("Diabetips NfcReader", "S: $s")
//                Log.d("Diabetips NfcReader", "Next read: " + s.substring(4, 6))
//                val current = s.substring(4, 6).toInt(16)
//                Log.d("Diabetips NfcReader", "Next read: $current")
//                Log.d("Diabetips NfcReader", "Next historic read " + s.substring(6, 8))
//                val bloque1 = arrayOfNulls<String>(16)
//                val bloque2 = arrayOfNulls<String>(32)
//                Log.d(
//                    "Diabetips NfcReader",
//                    "--------------------------------------------------"
//                )
//                var ii = 0
//                run {
//                    var i = 8
//                    while (i < 8 + 15 * 12) {
//                        Log.d("Diabetips NfcReader", s.substring(i, i + 12))
//                        bloque1[ii] = s.substring(i, i + 12)
//                        val g = s.substring(i + 2, i + 4) + s.substring(i, i + 2)
//                        if (current == ii) {
//                            currentGlucose = glucoseReading(g.toInt(16))
//                        }
//                        ii++
//                        i += 12
//                    }
//                }
//                lectura = lectura + "Current approximate glucose " + currentGlucose
//                Log.d(
//                    "Diabetips NfcReader",
//                    "Current approximate glucose $currentGlucose"
//                )
//                Log.d(
//                    "Diabetips NfcReader",
//                    "--------------------------------------------------"
//                )
//                ii = 0
//                var i = 188
//                while (i < 188 + 31 * 12) {
//                    Log.d("Diabetips NfcReader", s.substring(i, i + 12))
//                    bloque2[ii] = s.substring(i, i + 12)
//                    ii++
//                    i += 12
//                }
//                Log.d(
//                    "Diabetips NfcReader",
//                    "--------------------------------------------------"
//                )
//            } catch (e: IOException) {
//                activity.runOnUiThread(Runnable {
//                    Toast.makeText(
//                        activity.getApplicationContext(),
//                        "Error reading NFC!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
//                return null
//            }
//            addText(lectura!!)
//            try {
//                nfcvTag.close()
//            } catch (e: IOException) {
//                /*
//                Abbott.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Error closing NFC connection!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return null;
//                */
//            }
///*            val mp: MediaPlayer
//            mp = MediaPlayer.create(activity, R.raw.notification)
//            mp.setOnCompletionListener { mp -> // TODO Auto-generated method stub
//                var mp = mp
//                mp!!.reset()
//                mp!!.release()
//                mp = null
//            }
//            mp.start()*/
//            val date = Date()
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
//            val myFile = File("/sdcard/fsl_" + dateFormat.format(date).toString() + ".log")
//            try {
//                myFile.createNewFile()
//                val fOut = FileOutputStream(myFile)
//                val myOutWriter = OutputStreamWriter(fOut)
//                myOutWriter.append(lectura)
//                myOutWriter.close()
//                fOut.close()
//            } catch (e: Exception) {
//            }
//            return null
//        }
//    }
//
//    private fun addText(s: String) {
//        activity.runOnUiThread(Runnable { tvResult!!.text = s })
//    }
//
//    private fun GetTime(minutes: Long) {
//        val t4 = minutes / 1440
//        val t5 = minutes - t4 * 1440
//        val t6 = t5 / 60
//        val t7 = t5 - t6 * 60
//    }
//
//    private fun glucoseReading(`val`: Int): Float {
//        // ((0x4531 & 0xFFF) / 6) - 37;
//        val bitmask = 0x0FFF
//        return java.lang.Float.valueOf(java.lang.Float.valueOf((`val` and bitmask) / 6.toFloat()) - 37)
//    }
//
////    protected val hexArray = "0123456789ABCDEF".toCharArray()
////    fun bytesToHex(bytes: ByteArray): String? {
////        val hexChars = CharArray(bytes.size * 2)
////        for (j in bytes.indices) {
////            val v = (bytes[j] and 0xFF).toInt()
////            hexChars[j * 2] = hexArray.get(v ushr 4)
////            hexChars[j * 2 + 1] = hexArray.get(v and 0x0F)
////        }
////        return String(hexChars)
////    }
//
//    fun bytesToHex(src: ByteArray?): String {
//            val builder = StringBuilder("")
//            if (src == null || src.size <= 0) {
//                return ""
//            }
//            val buffer = CharArray(2)
//            for (b in src) {
//                buffer[0] =
//                    Character.forDigit(((b.toInt() ushr 4).toByte() and 0x0F.toByte()).toInt(), 16)
//                buffer[1] = Character.forDigit((b and 0x0F.toByte()).toInt(), 16)
//                builder.append(buffer)
//            }
//            return builder.toString()
//    }


    /**
     *
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     */
    private inner class NfcVReaderTask : AsyncTask<Tag?, Void?, String?>() {

        override fun onPostExecute(result: String?) {
            val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000)
            //Abbott.this.finish();
        }

        fun bytesToHexString(src: ByteArray?): String {
            val builder = StringBuilder("")
            if (src == null || src.size <= 0) {
                return ""
            }
            val buffer = CharArray(2)
            for (b in src) {
                buffer[0] =
                    Character.forDigit(((b.toInt() ushr 4).toByte() and 0x0F.toByte()).toInt(), 16)
                buffer[1] = Character.forDigit((b and 0x0F.toByte()).toInt(), 16)
                builder.append(buffer)
            }
            return builder.toString()
        }


        override fun doInBackground(vararg params: Tag?): String? {
            val tag = params[0]
            val nfcvTag = NfcV.get(tag)
            Log.d("diabetips", "Enter NdefReaderTask: $nfcvTag")
            Log.d("diabetips", "Tag ID: " + tag!!.id)
            sensorTagId = bytesToHexString(tag.id)
            readNfcTag(tag)
            val raw = RawTagData(sensorTagId, data)
            Log.d("diabetips", "Date : " + raw.date)
            Log.d("diabetips", "Tag Id : " + raw.tagId)
            Log.d("diabetips", "Sensor age : " + raw.sensorAgeInMinutes)
            Log.d(
                "diabetips",
                "TimeZone Offset : " + raw.timezoneOffsetInMinutes / 60
            )
            val indexHistory = raw.getIndexHistory()
            val glucoseLevels = ArrayList<Int>()
            val ageInSensorMinutesList = ArrayList<Int>()
            val mostRecentHistoryAgeInMinutes: Int =
                3 + (raw.sensorAgeInMinutes - 3) % historyIntervalInMinutes
            val sensor = SensorData(raw)
            val glucoseDatas = ArrayList<GlucoseData>()
            // read history values from ring buffer, starting at indexHistory (bytes 124-315)
            for (counter in 0 until numHistoryValues) {
                val index: Int = (indexHistory + counter) % numHistoryValues
                val glucoseLevelRaw = raw.getHistoryValue(index)
                // skip zero values if the sensor has not filled the ring buffer yet completely
                if (glucoseLevelRaw > 0) {
                    val dataAgeInMinutes: Int =
                        mostRecentHistoryAgeInMinutes + (numHistoryValues - (counter + 1)) * historyIntervalInMinutes
                    val ageInSensorMinutes = raw.sensorAgeInMinutes - dataAgeInMinutes

                    // skip the first hour of sensor data as it is faulty
                    if (ageInSensorMinutes > minSensorAgeInMinutes) {
                        glucoseLevels.add(glucoseLevelRaw)
                        ageInSensorMinutesList.add(ageInSensorMinutes)
                        val glucoseData = GlucoseData(
                            sensor,
                            raw.sensorAgeInMinutes,
                            raw.timezoneOffsetInMinutes,
                            glucoseLevelRaw,
                            true,
                            ageInSensorMinutes.toLong()
                        )
                        glucoseDatas.add(glucoseData)
                    }
                }
            }
            try {
                val fos: FileOutputStream = context.applicationContext.openFileOutput(
                    "glucose",
                    Context.MODE_PRIVATE
                )
                val oos = ObjectOutputStream(fos)
                oos.writeObject(glucoseDatas)
                oos.close()
                fos.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
            var bs: BloodSugarObject = BloodSugarObject()
            bs.interval = historyIntervalInMinutes * 60
            if (glucoseDatas.size <= 0) {
                Toast.makeText(context, "Le capteur n'est pas encore pret", Toast.LENGTH_SHORT)
                    .show()
                return null;
            }
            bs.start = sensor.startDate / 1000 + glucoseDatas[0].date * 60
            bs.measures = glucoseLevels.map { it -> it / 10 }.toTypedArray()
            Log.d("MEASURES", bs.measures.joinToString(","))
            Log.d("LAST", bs.measures[bs.measures.size - 1].toString())
            Log.d("END", bs.measures[0].toString())

            BloodSugarService.instance.postMeasures(bs).doOnSuccess(){
                if (it.second.component2() == null) {
                } else {
                    Log.d("BLOOD", it.second.component2()!!.exception.message)
                }
            }.subscribe()
            return null
        }
    }

    private fun readNfcTag(tag: Tag): Boolean {
        val nfcvTag = NfcV.get(tag)
        Log.d(LOG_ID, "Attempting to read tag data")
        try {
            nfcvTag.connect()
            val uid = tag.id
            val step = if (NFC_USE_MULTI_BLOCK_READ) 3 else 1
            val blockSize = 8
            var blockIndex = 0
            while (blockIndex <= 40) {
                var cmd: ByteArray
                if (NFC_USE_MULTI_BLOCK_READ) {
                    cmd = byteArrayOf(
                        0x02,
                        0x23,
                        blockIndex.toByte(),
                        0x02
                    ) // multi-block read 3 blocks
                } else {
                    cmd = byteArrayOf(
                        0x60,
                        0x20,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        blockIndex.toByte(),
                        0
                    )
                    System.arraycopy(uid, 0, cmd, 2, 8)
                }
                var readData: ByteArray
                val startReadingTime = System.currentTimeMillis()
                while (true) {
                    try {
                        readData = nfcvTag.transceive(cmd)
                        break
                    } catch (e: IOException) {
                        if (System.currentTimeMillis() > startReadingTime + nfcReadTimeout) {
                            Log.e(LOG_ID, "tag read timeout")
                            return false
                        }
                    }
                }
                if (NFC_USE_MULTI_BLOCK_READ) {
                    System.arraycopy(
                        readData,
                        1,
                        data,
                        blockIndex * blockSize,
                        readData.size - 1
                    )
                } else {
                    readData = Arrays.copyOfRange(readData, 2, readData.size)
                    System.arraycopy(readData, 0, data, blockIndex * blockSize, blockSize)
                }
                blockIndex += step
            }
            Log.d(LOG_ID, "Got NFC tag data")
        } catch (e: Exception) {
            Log.i(LOG_ID, e.toString())
            return false
        } finally {
            try {
                nfcvTag.close()
            } catch (e: Exception) {
                Log.e(LOG_ID, "Error closing tag!")
            }
        }
        Log.d(LOG_ID, "Tag data reader exiting")
        Log.d(LOG_ID, Arrays.toString(data))
        return true
    }
}