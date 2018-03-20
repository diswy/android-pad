package gorden.lib.video

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.anko.static.dp
import kotlinx.android.synthetic.main.mediatitle.view.*

@SuppressLint("ViewConstructor")
class ExMediaTitle(videoView:ExVideoView) : LinearLayout(videoView.context){
    init {
        LayoutInflater.from(context).inflate(R.layout.mediatitle,this)

        btn_back.setOnClickListener {
            videoView.exitFullScreen()
        }
    }


    fun setTitle(title:String){
        text_title.text = title
    }

    fun visibility(show:Boolean){
        btn_back.visibility = if (show) {
            (text_title.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
            View.VISIBLE} else {
            (text_title.layoutParams as LinearLayout.LayoutParams).leftMargin = 10.dp
            View.GONE}
    }
}