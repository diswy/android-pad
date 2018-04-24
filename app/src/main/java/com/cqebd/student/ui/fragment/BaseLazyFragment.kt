package com.cqebd.student.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



/**
 * laze fragment.
 *
 */
abstract class BaseLazyFragment : Fragment() {
    private lateinit var mViewContent:View
    open var mIsVisible: Boolean = false
    open var mIsInit: Boolean = false// 是否初始化了界面

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(userVisibleHint && mIsInit) {
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

        initView()
        bindEvents()

        return mViewContent
    }


    override fun onDestroyView() {
        mIsVisible = false
        mIsInit = false
        super.onDestroyView()
    }

    private fun onVisible() {
        lazyLoad()
    }

    protected abstract fun lazyLoad()

    protected abstract fun onInvisible()

    protected abstract fun getLayoutRes():Int

    open fun initView(){
        mIsInit = true
    }

    open fun bindEvents(){

    }

}
