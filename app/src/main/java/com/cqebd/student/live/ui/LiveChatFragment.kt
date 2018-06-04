package com.cqebd.student.live.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_live_chat.*


/**
 * Live Chat
 *
 */
class LiveChatFragment : BaseFragment() {
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_live_chat, container, false)
    }

    fun onCurrent(){
        mTv.text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    }


}
