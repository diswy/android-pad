package com.cqebd.student.widget.refresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.widget.LoadingView
import gorden.refresh.KRefreshHeader
import gorden.refresh.KRefreshLayout

/**
 * 描述
 * Created by gorden on 2017/9/18.
 */
class RefreshHeader(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), KRefreshHeader {

    constructor(context: Context) : this(context, null)

    private val arrawImg: ImageView
    private val textTitle: TextView
    private val loadView: LoadingView

    init {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.HORIZONTAL
        root.gravity = Gravity.CENTER_VERTICAL
        addView(root, FrameLayout.LayoutParams.WRAP_CONTENT, 40.dp)
        (root.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER

        arrawImg = ImageView(context)
        arrawImg.setImageResource(R.drawable.ic_arrow_down)
//        arrawImg.scaleType = ImageView.ScaleType.CENTER
        root.addView(arrawImg,30.dp,30.dp)

        loadView = LoadingView(context)
        loadView.visibility = View.GONE
        root.addView(loadView,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        textTitle = TextView(context)
        textTitle.text = "下拉刷新..."
        textTitle.setTextColor(Color.parseColor("#666666"))
        val params = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.leftMargin = 10.dp
        root.addView(textTitle, params)

        setPadding(0, 5.dp, 0, 5.dp)

    }

    private var isReset = true

    override fun failingRetention(): Long = 200

    override fun maxOffsetHeight(): Int = 4 * height

    override fun onComplete(refreshLayout: KRefreshLayout, isSuccess: Boolean) {
        arrawImg.visibility = View.VISIBLE
        loadView.visibility = View.GONE
        loadView.stop()
        if (isSuccess) {
            textTitle.text = "刷新完成..."
        } else {
            textTitle.text = "刷新失败..."
        }
    }

    override fun onPrepare(refreshLayout: KRefreshLayout) {
        textTitle.text = "下拉刷新..."
    }

    override fun onRefresh(refreshLayout: KRefreshLayout) {
        arrawImg.visibility = View.GONE
        loadView.visibility = View.VISIBLE
        loadView.start()
        textTitle.text = "加载中..."
        isReset = false
    }

    override fun onReset(refreshLayout: KRefreshLayout) {
        arrawImg.animate().rotation(0f).start()
        textTitle.text = "下拉刷新..."
        isReset = true
        arrawImg.visibility = View.VISIBLE
    }

    private var attain = false
    override fun onScroll(refreshLayout: KRefreshLayout, distance: Int, percent: Float, refreshing: Boolean) {
        if (!refreshing && isReset) {
            if (percent >= 1 && !attain) {
                attain = true
                textTitle.text = "释放刷新..."
                arrawImg.animate().rotation(-180f).start()
            } else if (percent < 1 && attain) {
                attain = false
                arrawImg.animate().rotation(0f).start()
                textTitle.text = "下拉刷新..."
            }
        }
    }

    override fun refreshHeight(): Int = height

    override fun succeedRetention(): Long = 200

}