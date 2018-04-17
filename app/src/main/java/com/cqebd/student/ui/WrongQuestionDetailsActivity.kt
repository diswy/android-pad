package com.cqebd.student.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.fragment.NestedWebFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.WrongQuestionDetails
import com.cqebd.student.vo.entity.WrongQuestionDetailsItem
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_wrong_question_details.*

class WrongQuestionDetailsActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    val mistakeUrl = "http://service.student.cqebd.cn/HomeWork/ErrorQustionAnswer?QuestionID=%s&StudentQuestionsTasksId=%s"

    private val mTaskId by lazy { intent.getIntExtra("taskId", -1) }
    private var mIsScrolled: Boolean = false    // viewpager是否处于惯性滑动
    private var mCurrentPos = 0                 // viewpager当前页

    override fun setContentView() {
        setContentView(R.layout.activity_wrong_question_details)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar_title.text = intent.getStringExtra("title")
        toolbar.setNavigationOnClickListener { finish() }
        getErrorQuestions()
        viewPager.addOnPageChangeListener(this)
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

    private fun initPager(list: List<WrongQuestionDetailsItem>) {
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val fragment: Fragment = WebFragment()
                val bundle = Bundle()
                bundle.putString("url", String.format(mistakeUrl, list[position].querstionId, mTaskId))
                fragment.arguments = bundle
                return fragment
            }

            override fun getCount(): Int {
                return list.size
            }

        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_DRAGGING -> mIsScrolled = false
            ViewPager.SCROLL_STATE_SETTLING -> mIsScrolled = true
            ViewPager.SCROLL_STATE_IDLE -> {
                if (!mIsScrolled && mCurrentPos != 0) {
                    toast("后面没有了")
                }
                mIsScrolled = true
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mCurrentPos = position
    }

}
