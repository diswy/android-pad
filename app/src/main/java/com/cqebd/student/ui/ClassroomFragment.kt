package com.cqebd.student.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 课堂
 * Created by gorden on 2018/2/26.
 */
class ClassroomFragment : BaseFragment() {
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_classroom,container,false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        pager_ad.setImagesUrl(arrayListOf("http://img3.imgtn.bdimg.com/it/u=133586149,2711567276&fm=11&gp=0.jpg"
        ,"https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=699537926,455842408&fm=11&gp=0.jpg"
        ,"https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=970551692,898694267&fm=27&gp=0.jpg"))
    }
}