package com.hbl.amc.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.hbl.amc.R


class CustomKeyBoardControlEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /*constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)*/

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.keyCode == KeyEvent.KEYCODE_BACK
            && event.action == KeyEvent.ACTION_UP
        ) {
            if (listener != null)
                listener!!.onStateChanged(this, false)
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        listener?.onStateChanged(this, true)
    }

    override fun setTypeface(tf: Typeface?, style: Int) {
        var tf = tf
        if (style == Typeface.BOLD) {
            tf = resources.getFont(R.font.bliss_medium)
            super.setTypeface(tf, style)
        } else {
            tf = resources.getFont(R.font.bliss_light)
            super.setTypeface(tf, style)
        }
    }


    /**
     * Keyboard Listener
     */
    var listener: KeyboardListener? = null

    fun setOnKeyboardListener(listener: KeyboardListener?) {
        this.listener = listener
    }

    interface KeyboardListener {
        fun onStateChanged(
            keyboardEditText: CustomKeyBoardControlEditText?,
            showing: Boolean
        )
    }
}