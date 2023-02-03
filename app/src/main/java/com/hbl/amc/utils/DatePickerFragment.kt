package com.hbl.amc.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hbl.amc.ui.FROM_DATE
import com.hbl.amc.ui.FUTURE_DATE_DISABLE
import com.hbl.amc.ui.PAST_DATE_DISABLE
import com.hbl.amc.ui.TO_DATE
import java.util.*

class DatePickerFragment(private val onDateSetListener: DatePickerDialog.OnDateSetListener) :
    DialogFragment() {

    var type = 0
    var isMinor = true
    var customDay: Int? = null
    var customMonth: Int? = null
    var customYear: Int? = null

    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        var year = 0
        year = if (isMinor) {
            c.get(Calendar.YEAR)
        } else {
            c.get(Calendar.YEAR) - 18
        }

        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        datePickerDialog =
            DatePickerDialog(requireActivity(), onDateSetListener, year, month, day)

        if (type == PAST_DATE_DISABLE) {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        } else if (type == FUTURE_DATE_DISABLE) {
            if (isMinor) {
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            } else {
                c.set(year, month, day)
                datePickerDialog.datePicker.maxDate = c.timeInMillis
            }
        } else if (type == TO_DATE) {
            var c: Calendar = Calendar.getInstance()
            if (customYear != null && customMonth != null && customDay != null) {
                c.set(customYear!!, customMonth!! - 1, customDay!!)
                datePickerDialog.datePicker.minDate = c.getTimeInMillis()
                datePickerDialog.setTitle("To Date")
                if (System.currentTimeMillis() > c.timeInMillis)
                    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            }
        } else if (type == FROM_DATE) {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.setTitle("From Date")
        }

        return datePickerDialog
    }


    //usage in any fragment on text input layout click
//    val newFragment = binding.cnicExpiryDate.editText?.let { it1 -> DatePickerFragment(it1) }
//    newFragment!!.show(childFragmentManager, "datePicker")
}