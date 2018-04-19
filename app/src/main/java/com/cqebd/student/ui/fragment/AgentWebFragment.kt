package com.cqebd.student.ui.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.js.VideoJs
import com.just.agentweb.AgentWeb
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_agent_web.*


class AgentWebFragment : BaseFragment() {
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_agent_web, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        if (arguments != null){
            val url = arguments?.getString("url")
            previewTask(url)
        }
    }

    private fun previewTask(url: String?) {
        if (TextUtils.isEmpty(url)) {
            throw IllegalArgumentException("H5地址不能为空")
        }
        Logger.d(url)

        val mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(web_container, FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url)

        mAgentWeb.jsInterfaceHolder.addJavaObject("video", VideoJs(activity))

    }

}
