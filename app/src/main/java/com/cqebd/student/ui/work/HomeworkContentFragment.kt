package com.cqebd.student.ui.work


import com.cqebd.student.R
import com.cqebd.student.ui.fragment.BaseLazyFragment

/**
 * 作业内容
 *
 */
class HomeworkContentFragment : BaseLazyFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_homework_content
    }

    override fun lazyLoad() {

    }

    override fun onInvisible() {

    }

    override fun initView() {
        arguments?.let {

        }
        super.initView()
    }


}
