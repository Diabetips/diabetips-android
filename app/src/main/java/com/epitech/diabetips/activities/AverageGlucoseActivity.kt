package com.epitech.diabetips.activities

import android.os.Bundle
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.ADiabetipsActivity
import kotlinx.android.synthetic.main.activity_average_glucose.*

class AverageGlucoseActivity : ADiabetipsActivity(R.layout.activity_average_glucose) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeAverageGlucoseButton.setOnClickListener {
            finish()
        }
        updateAverage(42f)
        //TODO average glucose
    }

    private fun updateAverage(average: Float? = null) {
        if (average != null) {
            averageGlucoseText.text = "${getText(R.string.average)} : ${average.toBigDecimal().stripTrailingZeros().toPlainString()} ${getString(R.string.unit_glucose)}"
        } else {
            averageGlucoseText.text = ""
        }
    }
}
