package com.example.telalogin

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimeAxisValueFormatter : ValueFormatter() {
    private val mFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return mFormat.format(Date(value.toLong()))
    }
}
