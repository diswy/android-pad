package com.xiaofu.lib_base_xiaofu.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger

/**
 * @Description:懒加载fragment，只有在fragmentAdapter中才会正确执行setUserVisibleHint
 * @Author:小夫
 * @Date:2018/8/1 16:25
 */
abstract class BaseLazyFragment : Fragment() {
    abstract fun getLayoutRes(): Int
    abstract fun lazyLoad()

    open var mInit: Boolean = false//视图是否创建完毕
    open var mVisible: Boolean = false//fragment是否可见


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mVisible = userVisibleHint //fragment是否可见
        Logger.wtf("参数：mVisible = $mVisible ; mInit = $mInit")
        if (mVisible && mInit) {
            lazyLoad()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutRes(), container, false)
        view.parent?.let {
            (it as ViewGroup).removeView(view)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mInit = true
        Logger.wtf("onViewCreated：mVisible = $mVisible")
        if (mVisible) {
            lazyLoad()
        }
    }

    override fun onDestroyView() {
        mInit = false
        super.onDestroyView()
    }
}