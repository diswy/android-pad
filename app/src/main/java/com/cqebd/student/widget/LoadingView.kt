package com.cqebd.student.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.anko.static.dp

/**
 * 描述
 * Created by gorden on 2017/11/10.
 */
class LoadingView : View {
    private val minSize = 30.dp
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = minSize.toFloat() / 12
    @ColorInt
    var color: Int = 0xFF0aaf73.toInt()

    private var bowSize: Float = minSize / 2 - radius
    private var center: Float = (minSize / 2).toFloat()

    private val rotateAnim: RotateAnimation by lazy {
        RotateAnimation(0f, 360f * 100, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
            duration = 1500 * 100
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSizeAndState(minSize, widthMeasureSpec, 0)
        val height = resolveSizeAndState(minSize, heightMeasureSpec, 0)
        val needSize = Math.min(width, height)
        setMeasuredDimension(width, height)
        radius = needSize.toFloat() / 16
        bowSize = needSize / 2 - radius
        center = needSize.toFloat() / 2
    }

    fun start(){
        startAnimation(rotateAnim)
    }

    fun stop(){
        rotateAnim.cancel()
        clearAnimation()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (position in (1..12)) {
            val x = center + Math.sin(position * Math.PI / 6) * bowSize
            val y = center - Math.cos(position * Math.PI / 6) * bowSize
            if (position < 6) {
                paint.color = Color.WHITE
                paint.alpha = 255
            } else {
                paint.color = color
                paint.alpha = ((position - 5).toFloat() / 7 * 255).toInt()
            }
            canvas?.drawCircle(x.toFloat(), y.toFloat(), radius, paint)
        }
    }
}