package com.cqebd.student.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.ui.fragment.NestedWebFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.WrongQuestionDetails
import com.cqebd.student.vo.entity.WrongQuestionDetailsItem
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_wrong_question_details.*

class WrongQuestionDetailsActivity : BaseActivity() {

    val mistakeUrl = "http://service.student.cqebd.cn/HomeWork/ErrorQustionAnswer?QuestionID=%s&StudentQuestionsTasksId=%s"

    private val mTaskId by lazy { intent.getIntExtra("taskId", -1) }

    override fun setContentView() {
        setContentView(R.layout.activity_wrong_question_details)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }
        getErrorQuestions()
    }

    private fun getErrorQuestions() {
        NetClient.workService().getErrorQuestions(mTaskId)
                .enqueue(object : NetCallBack<BaseResponse<WrongQuestionDetails>>() {
                    override fun onSucceed(response: BaseResponse<WrongQuestionDetails>?) {
                        Logger.d(response)

                        response?.data?.ErrorList.let {
                            initPager(it!!)
                        }
                    }

                    override fun onFailure() {

                    }

                })
    }

    private fun initPager(list :List<WrongQuestionDetailsItem>){
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager){
            override fun getItem(position: Int): Fragment {
                val fragment :Fragment = WebFragment()
                val bundle = Bundle()
                bundle.putString("url",String.format(mistakeUrl,list[position].querstionId,mTaskId))
                fragment.arguments = bundle
                return fragment
            }

            override fun getCount(): Int {
                return list.size
            }

        }
    }

}
