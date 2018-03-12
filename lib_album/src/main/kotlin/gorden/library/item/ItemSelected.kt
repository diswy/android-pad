package gorden.library.item

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import gorden.library.entity.dp

/**
 * 选中的照片
 */
class ItemSelected(context: Context?) : FrameLayout(context) {
    var imageView: ImageView
    init {
        setPadding(0,10.dp,0,10.dp)
        imageView = ImageView(context)
        imageView.setPadding(1.dp,1.dp,1.dp,1.dp)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(imageView,LayoutParams(60.dp,60.dp).apply {
            leftMargin = 5.dp
            rightMargin = 5.dp
        })
    }
}