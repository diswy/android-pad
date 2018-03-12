package gorden.library.item

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import gorden.library.R
import gorden.library.entity.dp


/**
 * 相机
 */

class ItemCamera(context: Context) : LinearLayout(context) {
    init {
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(Color.parseColor("#66000000"))
        val imageView = ImageView(context)
        imageView.setImageResource(android.R.drawable.ic_menu_camera)
        addView(imageView, 40.dp,40.dp)

        val textView = TextView(context)
        textView.text = "拍摄照片"
        textView.setTextColor(ContextCompat.getColor(context, R.color.album_textcolor))
        textView.textSize = 14f
        addView(textView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}
