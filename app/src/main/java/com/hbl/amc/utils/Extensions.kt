package com.hbl.amc.utils

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.hbl.amc.R
import com.hbl.amc.utils.AppUtils.afterTextChanged
import java.text.NumberFormat
import java.util.*


fun TextInputLayout.setTitleColour(context: Context) {
    //Here you get int representation of an HTML color resources
    val yourColorWhenEnabled = ContextCompat.getColor(context, R.color.hbl_main_green)
    val yourColorWhenDisabled = ContextCompat.getColor(context, R.color.gray_text)

// Here you get matrix of states, I suppose it is a matrix because using a matrix you can set the same color (you have an array of colors) for different states in the same array
    val states = arrayOf(
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_expanded)
    )

    defaultHintTextColor =
        ColorStateList(states, intArrayOf(yourColorWhenDisabled, yourColorWhenEnabled))

}


fun TextInputLayout.resetTitleColour(context: Context) {
    //Here you get int representation of an HTML color resources
    val yourColorWhenEnabled = ContextCompat.getColor(context, R.color.gray_text)
    val yourColorWhenDisabled = ContextCompat.getColor(context, R.color.hbl_main_green)

// Here you get matrix of states, I suppose it is a matrix because using a matrix you can set the same color (you have an array of colors) for different states in the same array
    val states = arrayOf(
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_expanded)
    )

    defaultHintTextColor =
        ColorStateList(states, intArrayOf(yourColorWhenDisabled, yourColorWhenEnabled))

}

fun EditText.setupEditText(context: Context, textInputLayout: TextInputLayout) {

    addTextChangedListener {
        textInputLayout.setTitleColour(context)
    }

    onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (text?.isEmpty()!! && !hasFocus) {
            textInputLayout.resetTitleColour(context)
        } else if (hasFocus) {
            textInputLayout.setTitleColour(context)
        }
    }
}

fun PerfectDecimal(str: String, MAX_BEFORE_POINT: Int, MAX_DECIMAL: Int): String? {
    var str = str
    if (str[0] == '.') str = "0$str"
    val max = str.length
    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char
    while (i < max) {
        t = str[i]
        if (t != '.' && !after) {
            up++
            if (up > MAX_BEFORE_POINT) return rFinal
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > MAX_DECIMAL) return rFinal
        }
        rFinal += t
        i++
    }
    return rFinal
}

fun <T> mergeLists(first: List<T>, second: List<T>): List<T> {
    return first + second
}

fun formatAmount(amount : Number) : String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    nf.minimumFractionDigits = 2

    return nf.format(amount)
}