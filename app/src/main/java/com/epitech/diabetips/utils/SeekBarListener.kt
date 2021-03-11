package com.epitech.diabetips.utils

import android.widget.SeekBar

class SeekBarListener(private val callback: ((Int) -> Unit)): SeekBar.OnSeekBarChangeListener {

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        callback.invoke(progress)
    }
}