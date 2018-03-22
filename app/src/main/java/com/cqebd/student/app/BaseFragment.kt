package com.cqebd.student.app

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * BaseFragment
 * Created by gorden on 2017/11/6.
 */
abstract class BaseFragment : Fragment() {
    var lifeListener: ViewLifeListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return setContentView(inflater, container, savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize(savedInstanceState)
        bindEvents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifeListener?.onInitialized()
    }

    /**
     * 绑定布局内容
     */
    abstract fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?

    /**
     * 一些初始化工作
     */
    open fun initialize(savedInstanceState: Bundle?) {}

    /**
     * 事件绑定
     */
    open fun bindEvents() {}

    interface ViewLifeListener {
        fun onInitialized()
    }
}