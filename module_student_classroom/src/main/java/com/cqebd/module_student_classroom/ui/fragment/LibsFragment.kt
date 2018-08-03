package com.cqebd.module_student_classroom.ui.fragment


import android.app.Activity
import com.cqebd.module_student_classroom.R
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.base.BaseFragment

/**
 * @Description:教学资源
 * @Author:小夫
 * @Date:2018/8/1 16:06
 */
class LibsFragment : BaseFragment() {

    override fun getLayoutRes(): Int = R.layout.fragment_libs

    override fun initialize(activity: Activity) {
        Logger.d("libs    $this")
    }
}
