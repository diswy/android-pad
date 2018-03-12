package gorden.library.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import gorden.library.R
import gorden.library.entity.dp

@Suppress("JoinDeclarationAndAssignment")
@SuppressLint("ViewConstructor")
/**
 * 列表 item
 * Created by Gorden on 2017/4/2.
 */

class ItemPicture(context: Context, imgSize: Int) : FrameLayout(context) {
    var imgPicture: AppCompatImageView
    var imgCheck: CheckBox
    var viewShadow: View

    init {
        imgPicture = AppCompatImageView(context)
        imgPicture.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(imgPicture, imgSize, imgSize)

        viewShadow = View(context)
        viewShadow.setBackgroundColor(Color.parseColor("#88000000"))
        viewShadow.visibility = View.GONE
        addView(viewShadow, imgSize, imgSize)

        imgCheck = CheckBox(context)
        imgCheck.setButtonDrawable(R.drawable.album_checkbox)
        val paramsCheckbox = FrameLayout.LayoutParams(30.dp, 30.dp)
        paramsCheckbox.gravity = Gravity.END or Gravity.TOP
        addView(imgCheck, paramsCheckbox)
    }
}
