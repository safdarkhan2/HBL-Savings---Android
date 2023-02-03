package com.hbl.amc.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment (private val editText: EditText) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    lateinit var timePickerDialog: TimePickerDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        timePickerDialog = TimePickerDialog(requireActivity(), this, hour, minute, false)

        // Create a new instance of TimePickerDialog and return it
        return timePickerDialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        var am_pm = ""
        val datetime = Calendar.getInstance()
        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
        datetime[Calendar.MINUTE] = minute
        if (datetime[Calendar.AM_PM] == Calendar.AM) am_pm =
            "AM" else if (datetime[Calendar.AM_PM] == Calendar.PM) am_pm = "PM"
        val strHrsToShow =
            if (datetime[Calendar.HOUR] == 0) "12" else datetime[Calendar.HOUR].toString() + ""

        val min = datetime[Calendar.MINUTE]
        if(datetime[Calendar.MINUTE] < 10)
        {
            editText.setText("$strHrsToShow:0$min$am_pm")
        }
        else
        {
            editText.setText("$strHrsToShow:$min$am_pm")
        }
    }
}