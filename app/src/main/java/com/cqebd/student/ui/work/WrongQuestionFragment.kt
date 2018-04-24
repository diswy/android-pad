package com.cqebd.student.ui.work


import com.cqebd.student.R
import com.cqebd.student.ui.fragment.BaseLazyFragment

/**
 * 错题本
 *
 */
class WrongQuestionFragment : BaseLazyFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_wrong_question
    }

    override fun lazyLoad() {
        println("-------->WrongQuestionFragment可见")
    }

    override fun onInvisible() {
        println("-------->WrongQuestionFragment不可见")
    }

    override fun initView() {
        arguments?.let {

        }

        super.initView()
    }

}
