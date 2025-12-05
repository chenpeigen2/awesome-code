package com.example.apprenderapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.SurfaceView

class SurfaceViewHandWriting
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SurfaceView(context, attrs, defStyleAttr) {
    private val mPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true

    }

    private val path = Path().apply {
        moveTo(0f, 100f)
    }
}