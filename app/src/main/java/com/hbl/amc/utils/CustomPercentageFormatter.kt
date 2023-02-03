package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import java.text.DecimalFormat

/**
 * This IValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
class CustomPercentageFormatter() : ValueFormatter() {
    var mFormat: DecimalFormat
    private var pieChart: PieChart? = null

    // Can be used to remove percent signs if the chart isn't in percent mode
    constructor(pieChart: PieChart?) : this() {
        this.pieChart = pieChart
    }

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble()) + " %"
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
        return if (pieChart != null && pieChart!!.isUsePercentValuesEnabled) {
            // Converted to percent
            getFormattedValue(value)
        } else {
            // raw value, skip percent sign
            mFormat.format(value.toDouble())
        }
    }

    init {
        mFormat = DecimalFormat("###,###,##0")
    }
}