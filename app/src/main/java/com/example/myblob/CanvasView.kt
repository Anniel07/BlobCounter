package com.example.myblob

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * This class hold the canvas view
 */

class CanvasView : View {


    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val paint: Paint = Paint() // Set up the paint with which to draw.

    private var mainActivity: MainActivity


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mainActivity = context as MainActivity
    }

    /**
     * Called whenever the view changes size.
     * Since the view starts out with no size, this is also called after
     * the view has been inflated and has a valid size.
     */

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)

        mainActivity.setDim(w,h)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }


    fun drawSquare(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        paint.color = color
        paint.style = Paint.Style.FILL
        extraCanvas.drawRect(left, top, right , bottom, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        extraCanvas.drawRect(left + 0.5F, top + 0.5F, right + 0.5F, bottom + 0.5F, paint)
    }

    /**
     * Implement touch events
     */

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> mainActivity.mousePressed(event)
        }
        return true
    }

}