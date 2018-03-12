package com.cqebd.student.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.tools.colorForRes

/**
 * 描述
 * Created by gorden on 2017/11/10.
 */
class PageLoadView(context: Context, attrs: AttributeSet? = null) :FrameLayout(context,attrs){
    private val lin_root = LinearLayout(context)
    private val loadingView = LoadingView(context)
    private val imageView = ImageView(context)
    private val textMsg = TextView(context)
    var show:Boolean = true

    init {
        setBackgroundColor(colorForRes(R.color.color_f0))
        isClickable = true

        lin_root.orientation = LinearLayout.VERTICAL
        lin_root.gravity = Gravity.CENTER
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        addView(lin_root,lp)

        lin_root.addView(loadingView,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        lin_root.addView(imageView,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        lin_root.addView(textMsg,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        (textMsg.layoutParams as LinearLayout.LayoutParams).topMargin = 30.dp

        loadingView.visibility = View.GONE
        imageView.visibility = View.GONE
    }

    fun load(){
        if (!show) return
        visibility = View.VISIBLE

        loadingView.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        textMsg.text = "努力加载中..."
        loadingView.start()
        lin_root.setOnClickListener(null)
    }

    fun error(reload:()->Unit,msg:String = "加载失败，点我重试"){
        if (!show) return
        loadingView.visibility = View.GONE
        loadingView.stop()
        imageView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.ic_load_error)
        textMsg.text = msg
        lin_root.setOnClickListener {
            reload()
            load()
        }
    }

    fun dataEmpty(msg:String = "没有数据"){
        if (!show) return
        visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        loadingView.stop()
        imageView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.ic_load_empty)
        (imageView.layoutParams as LinearLayout.LayoutParams).leftMargin = 15.dp
        textMsg.text = msg
    }

    fun hide(){
        if (!show) return
        show = false
        loadingView.stop()
        visibility = View.GONE
    }
}