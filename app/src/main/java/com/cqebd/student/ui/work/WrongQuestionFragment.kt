package com.cqebd.student.ui.work


import com.cqebd.student.R
import com.cqebd.student.ui.fragment.BaseLazyFragment
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import org.jetbrains.anko.support.v4.toast

/**
 * 错题本
 *
 */
class WrongQuestionFragment : BaseLazyFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_wrong_question
    }

    override fun lazyLoad() {
        RxBus.get().register(this)
        println("-------->WrongQuestionFragment可见")
    }

    override fun onInvisible() {
        RxBus.get().unRegister(this)
        println("-------->WrongQuestionFragment不可见")
    }

    override fun initView() {
        arguments?.let {

        }

        super.initView()
    }


    @Subscribe(code = 0x10000)
    fun getBack(data:String) {
        toast("No.2 tmd success two!!! $data")
    }
}
