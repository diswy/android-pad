package gorden.library.item

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import gorden.library.R
import gorden.library.entity.dp

/**
 * 相册目录
 */

class ItemDir(context: Context) : LinearLayout(context) {
    lateinit var imgDir: AppCompatImageView
    lateinit var textDir: TextView
    lateinit var textCount: TextView
    lateinit var viewSelected: ImageView

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        orientation = LinearLayout.HORIZONTAL
        val padding = 10.dp
        setPadding(padding, padding, padding, padding)

        val frameLayout = FrameLayout(context)
        frameLayout.setBackgroundResource(R.drawable.photo_ic_dir_bg)
        addView(frameLayout, 80.dp,80.dp)
        frameLayout.setPadding(0, 0,5.dp,5.dp)

        imgDir = AppCompatImageView(context)
        imgDir.setBackgroundColor(Color.LTGRAY)
        imgDir.scaleType = ImageView.ScaleType.CENTER_CROP
        val paramsDir = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        frameLayout.addView(imgDir, paramsDir)

        val textLayout = LinearLayout(context)
        textLayout.orientation = LinearLayout.VERTICAL
        val paramsLin = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsLin.weight = 1f
        paramsLin.gravity = Gravity.CENTER_VERTICAL
        textLayout.setPadding(20.dp, 0, 20.dp, 0)
        addView(textLayout, paramsLin)

        textDir = TextView(context)
        textDir.setLines(1)
        textDir.textSize = 15f
        textDir.ellipsize = TextUtils.TruncateAt.END
        val paramsText = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsText.bottomMargin = 5.dp
        textLayout.addView(textDir, paramsText)

        textCount = TextView(context)
        textCount.textSize = 14f
        textCount.setTextColor(Color.LTGRAY)
        textLayout.addView(textCount)

        viewSelected = ImageView(context)
        viewSelected.setBackgroundResource(R.drawable.album_check)
        val paramsSelected = LinearLayout.LayoutParams(20.dp, 20.dp)
        paramsSelected.rightMargin = 20
        paramsSelected.gravity = Gravity.CENTER_VERTICAL
        addView(viewSelected, paramsSelected)
        viewSelected.visibility = View.GONE

    }
}
