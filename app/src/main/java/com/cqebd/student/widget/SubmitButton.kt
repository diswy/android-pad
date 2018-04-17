package com.cqebd.student.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.cqebd.student.R

/**
 * 提交按钮
 * Created by gorden on 2017/11/7.
 */
class SubmitButton(context: Context?, attrs: AttributeSet?=null) : TextView(context, attrs) {
    private val COLOR_DEFAULT: Int = 0xFF0DC984.toInt()
    private val COLOR_PRESSED: Int = 0xFF0DC984.toInt()
    private val COLOR_ENABLE: Int = 0X66000000

    var colorDefault: Int
    var colorPressed: Int
    var colorEnable: Int

    private var radius: Float
    private var mRadius: Float = 0f

    init {
        val a = context?.obtainStyledAttributes(attrs, R.styleable.SubmitButton)
        colorDefault = a?.getColor(R.styleable.SubmitButton_s_color_default, COLOR_DEFAULT)?:COLOR_DEFAULT
        colorPressed = a?.getColor(R.styleable.SubmitButton_s_color_pressed, COLOR_PRESSED)?:COLOR_PRESSED
        colorEnable = a?.getColor(R.styleable.SubmitButton_s_color_enable, COLOR_ENABLE)?:COLOR_ENABLE
        radius = a?.getDimension(R.styleable.SubmitButton_s_radius, -1f)?:-1f
        a?.recycle()
        gravity = Gravity.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRadius = if (radius == -1f) (measuredHeight / 2).toFloat() else radius
        setStateDrawable()
    }

    /**
     * 为Button设置颜色
     */
    private fun setStateDrawable() {
        val def = GradientDrawable()
        def.cornerRadius = mRadius
        def.setColor(colorDefault)

        val pre = GradientDrawable()
        pre.cornerRadius = mRadius
        pre.setColor(colorPressed)

        val ena = GradientDrawable()
        ena.cornerRadius = mRadius
        ena.setColor(colorEnable)

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), ena)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), pre)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), def)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = stateListDrawable
        } else {
            setBackgroundDrawable(stateListDrawable)
        }
    }

    fun setRadius(radius: Float) {
        this.radius = radius
        setStateDrawable()
    }

    fun setColors(def: Int, pressed: Int, enable: Int) {
        this.colorDefault = def
        colorPressed = pressed
        colorEnable = enable
        setStateDrawable()
    }

//    fun colorFull(){
//        this.colorDefault = colorForRes(R.color.state_right)
//        colorPressed = colorForRes(R.color.state_right_p)
//        setStateDrawable()
//    }
//    fun colorHalf(){
//        this.colorDefault = colorForRes(R.color.state_half)
//        colorPressed = colorForRes(R.color.state_half_p)
//        setStateDrawable()
//    }
//    fun colorError(){
//        this.colorDefault = colorForRes(R.color.state_error)
//        colorPressed = colorForRes(R.color.state_error_p)
//        setStateDrawable()
//    }
//    fun colorUnRead(){
//        this.colorDefault = colorForRes(R.color.state_no_read)
//        colorPressed = colorForRes(R.color.state_no_read_p)
//        setStateDrawable()
//    }
//    fun colorUnDo(){
//        this.colorDefault = colorForRes(R.color.state_no_work)
//        colorPressed = colorForRes(R.color.state_no_work_p)
//        setStateDrawable()
//    }
}