package com.cqebd.student.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.tools.colorForRes

/**
 * 描述
 * Created by gorden on 2017/11/14.
 */
class LoadingDialog : DialogFragment() {
    private var progressTitle: TextView? = null
    private lateinit var loadingView: LoadingView

    var progressMsg: String = "请稍后...."
        set(value) {
            field = value
            progressTitle?.text = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }

        progressTitle = TextView(context)
        progressTitle?.textSize = 16f
        progressTitle?.setTextColor(colorForRes(R.color.colorPrimary))
        progressTitle?.text = progressMsg

        val rootView = LinearLayout(context)
        rootView.orientation = LinearLayout.HORIZONTAL
        rootView.gravity = Gravity.CENTER_VERTICAL
        val padding = 10.dp
        rootView.setPadding(padding, padding, padding * 3, padding)

        loadingView = LoadingView(context)
        rootView.addView(loadingView, 45.dp, 45.dp)
        rootView.addView(progressTitle)
        (progressTitle?.layoutParams as? LinearLayout.LayoutParams)?.leftMargin = 20.dp

        return rootView
    }


    fun show(manager: FragmentManager?) {
        super.show(manager, "loading")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadingView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingView.stop()
    }
}