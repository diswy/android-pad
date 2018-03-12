package gorden.lib.anko

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.RawRes
import android.support.v4.content.ContextCompat
import android.view.View

/**
 * GradientDrawable 生成
 */
object KGradient {
    fun gradientDrawable(radius:FloatArray,color:Int,strokeWidth:Int,strokeColor:Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        drawable.setStroke(strokeWidth,strokeColor)
        drawable.cornerRadii = floatArrayOf(radius[0], radius[0], radius[1], radius[1], radius[2], radius[2], radius[3], radius[3])
        return drawable
    }

    fun gradientDrawable(radius:Float,color:Int,strokeWidth:Int,strokeColor:Int)
            = gradientDrawable(floatArrayOf(radius,radius,radius,radius),color,strokeWidth,strokeColor)

    fun gradientDrawable(radius:FloatArray,color:Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        drawable.cornerRadii = floatArrayOf(radius[0], radius[0], radius[1], radius[1], radius[2], radius[2], radius[3], radius[3])
        return drawable
    }

    fun gradientDrawable(radius:Float,color:Int)
            = gradientDrawable(floatArrayOf(radius,radius,radius,radius),color)

    fun viewPressed(view:View,radius: Float,defColor:Int,preColor:Int){
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), gradientDrawable(radius,preColor))
        stateListDrawable.addState(intArrayOf(), gradientDrawable(radius,defColor))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background=stateListDrawable
        }else{
            view.setBackgroundDrawable(stateListDrawable)
        }
    }
}