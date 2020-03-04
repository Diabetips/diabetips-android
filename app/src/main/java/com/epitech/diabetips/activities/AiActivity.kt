package com.epitech.diabetips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.epitech.diabetips.R
import kotlinx.android.synthetic.main.activity_ai.*
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.IOException
import java.nio.MappedByteBuffer


class AiActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        //        textView_msg!!.text = "Selected : "+languages[position]
        try {
            val json_string = assets.open("data/" + spinnerArray[position] + "/data.json").bufferedReader().use {
                it.readText()
            }
            val text = "Insulin : "  + json_string.takeLast(3).dropLast(1)
            this.editText.setText(text)
        } catch( e: IOException) {

        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
    var spinner: Spinner? = null

    private var tfliteModel: MappedByteBuffer? = null
    private val tfliteOptions = Interpreter.Options()
    var spinnerArray = arrayOf("Select a directory")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai)


        backButton.setOnClickListener {
            finish()
        }
//        tfliteModel = FileUtil.loadMappedFile(this, "model.tflite")
//        var tflite = Interpreter(tfliteModel!!, tfliteOptions)
        spinnerSetup()
    }

    fun spinnerSetup() {
        println(assets.list("data")?.get(0))
        assets.list("data")!!.forEach {
            spinnerArray += it.toString()
        }
        spinner = this.spinner_ids
        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)
    }


}
