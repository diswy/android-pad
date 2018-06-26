package com.cqebd.student.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R


/**
 * laze fragment.
 *
 */
abstract class BaseLazyFragment : Fragment() {
    protected lateinit var mViewContent: View
    open var mIsVisible: Boolean = false
    open var mIsInit: Boolean = false// 是否初始化了界面
    private var mVisible:Boolean = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        mVisible = userVisibleHint

        if (userVisibleHint && mIsInit) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInvisible()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewContent = inflater.inflate(getLayoutRes(), container, false)
        mViewContent.parent?.let {
            (it as ViewGroup).removeView(mViewContent)
        }
        return mViewContent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        bindEvents()
        if (mVisible)
            lazyLoad()
    }

    override fun onDestroyView() {
        mIsVisible = false
        mIsInit = false
        onInvisible()
        super.onDestroyView()
    }

    private fun onVisible() {
        lazyLoad()
    }

    protected abstract fun getLayoutRes(): Int

    protected abstract fun lazyLoad()

    protected abstract fun onInvisible()

    open fun initView() {
        mIsInit = true
    }

    open fun bindEvents() {

    }

}
