package com.hbl.amc.utils

import android.content.Context
import android.graphics.*
import android.graphics.Color.blue
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hbl.amc.R


class RiskIndicator @JvmOverloads constructor(context: Context,
                    attrs: AttributeSet? = null,
                    defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    var progress = 0
    var filledColor = R.color.yellow_theme

    override fun onDraw(canvas: Canvas) {

        // Do any additional setup after loading the view.
        super.onDraw(canvas)
//        val rect = RectF(80f, 140f, 340f, 400f)
        val height = (canvas.height / 2) - 40
        val widthc = (canvas.width / 2) + 10
        val diff = 150
        val rect = RectF((widthc - diff).toFloat(), (height - diff).toFloat(),
            (widthc + diff).toFloat(), (height + diff).toFloat()
        )


        var width = 10.0
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        var widthIncrease = 0.4
        val percentageIncrease = 2.5f
        val minAngle = 90f
        var currentAngle = minAngle

        for (i in 0..100) {

            if (i <= progress) {
                paint.color = ContextCompat.getColor(context, filledColor)
            } else {
                paint.color = ContextCompat.getColor(context, R.color.progress_indicator_green)
            }

            paint.strokeWidth = width.toFloat()

            if (currentAngle == 90f || currentAngle > 330f) {
                paint.strokeCap = Paint.Cap.ROUND
            } else {
                paint.strokeCap = Paint.Cap.SQUARE
            }

            val sweepAngle = currentAngle + percentageIncrease - currentAngle

            canvas.drawArc(rect, currentAngle, sweepAngle, false, paint)

            currentAngle += percentageIncrease
            width += widthIncrease
        }
    }
}