package com.xiaofu.lib_base_xiaofu.base

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    abstract fun getLayoutRes(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutRes(), container, false)
        view.parent?.let {
            (it as ViewGroup).removeView(view)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            initialize(it)
            bindEvent()
        }
    }

    protected open fun initialize(activity: Activity) {

    }

    protected open fun bindEvent() {

    }

}