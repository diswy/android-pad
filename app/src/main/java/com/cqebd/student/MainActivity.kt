package com.cqebd.student

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.event.STATUS_DATE
import com.cqebd.student.event.STATUS_QUESTION_TYPE
import com.cqebd.student.event.STATUS_SUBJECT
import com.cqebd.student.event.STATUS_TYPE
import com.cqebd.student.ui.HomeFragment
import com.cqebd.student.ui.MineFragment
import com.cqebd.student.ui.VideoFragment
import com.cqebd.student.ui.WorkFragment
import com.cqebd.student.ui.root.HomeworkFragment
import com.cqebd.student.ui.root.RootVideoFragment
import com.cqebd.student.vo.entity.FilterData
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import gorden.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawerlayout.*

class MainActivity : BaseActivity() {
    private var currentFragment: Fragment? = null

    override fun setContentView() {
        setContentView(R.layout.activity_main_drawerlayout)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        RxBus.get().register(this)
        initDrawerView()

        val titles = resources.getStringArray(R.array.title)
        navigation.addItem(
                BottomNavigationItem(R.drawable.ic_home_selected, titles[0]).setInactiveIconResource(R.drawable.ic_home_normal))
                .addItem(BottomNavigationItem(R.drawable.ic_video_selected, titles[1]).setInactiveIconResource(R.drawable.ic_video_normal))
                .addItem(BottomNavigationItem(R.drawable.ic_work_selected, titles[2]).setInactiveIconResource(R.drawable.ic_work_normal))
                .addItem(BottomNavigationItem(R.drawable.ic_mine_selected, titles[3]).setInactiveIconResource(R.drawable.ic_mine_normal))
                .initialise()

        navigation.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {
            }

            override fun onTabSelected(position: Int) {
                if (position == 2) {
                    main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                switchFragment(position)
            }
        })
        switchFragment(0)
    }

    private fun switchFragment(position: Int) {
        var targetFragment: Fragment? = supportFragmentManager.findFragmentByTag("tag$position")
        if (targetFragment == null) {
            when (position) {
                0 -> {
                    targetFragment = HomeFragment()
                }
                1 -> {
//                    targetFragment = VideoFragment()
                    targetFragment = RootVideoFragment()
                }
                2 -> {
//                    targetFragment = WorkFragment()
                    targetFragment = HomeworkFragment()
                }
                3 -> {
                    targetFragment = MineFragment()
                }
            }
        }
        val transaction = supportFragmentManager.beginTransaction().apply {
            if (currentFragment != null) {
                hide(currentFragment)
            }
        }
        if (targetFragment?.isAdded == true) {
            transaction.show(targetFragment).commitAllowingStateLoss()
        } else {
            transaction.add(R.id.frame_content, targetFragment, "tag$position").commitAllowingStateLoss()
        }
        currentFragment = targetFragment
    }

    /**
     * -------------------侧滑菜单部分-------------------
     */
    private var mSubjectPos = 0     // 学科
    private var mTypePos = 0        // 类型
    private var mQuestionTypePos = 0// 题型
    private var mDatePos = 0        // 日期
    private lateinit var mProblemTypeAdapter: TagAdapter<FilterData>
    private lateinit var mSubjectAdapter: TagAdapter<FilterData>
    private lateinit var mQuestionTypeAdapter: TagAdapter<FilterData>
    private lateinit var mDateAdapter: TagAdapter<FilterData>
    private fun initDrawerView() {
        val mInflater = LayoutInflater.from(this)
        // 禁用手势侧滑
        main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // 类型适配
        mProblemTypeAdapter = object : TagAdapter<FilterData>(FilterData.jobType) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        type_layout.adapter = mProblemTypeAdapter
        type_layout.setOnTagClickListener { _, position, _ ->
            mTypePos = position
            if (mTypePos == position) {
                mProblemTypeAdapter.setSelectedList(position)
            }
            return@setOnTagClickListener true
        }
        // 学科适配
        mSubjectAdapter = object : TagAdapter<FilterData>(FilterData.subjectAll) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        subject_layout.adapter = mSubjectAdapter
        subject_layout.setOnTagClickListener { _, position, _ ->
            mSubjectPos = position
            if (mSubjectPos == position) {
                mSubjectAdapter.setSelectedList(position)
            }
            return@setOnTagClickListener true
        }
        // 题型适配
        mQuestionTypeAdapter = object : TagAdapter<FilterData>(FilterData.problemType) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        question_type_layout.adapter = mQuestionTypeAdapter
        question_type_layout.setOnTagClickListener { _, position, _ ->
            mQuestionTypePos = position
            if (mQuestionTypePos == position) {
                mQuestionTypeAdapter.setSelectedList(position)
            }
            return@setOnTagClickListener true
        }
        // 日期适配
        mDateAdapter = object : TagAdapter<FilterData>(FilterData.dateFilter) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        date_layout.adapter = mDateAdapter
        date_layout.setOnTagClickListener { _, position, _ ->
            mDatePos = position
            if (mDatePos == position) {
                mDateAdapter.setSelectedList(position)
            }
            return@setOnTagClickListener true
        }

        main_drawer_btn_clear.setOnClickListener {
            main_drawer_layout.closeDrawer(Gravity.END)
            // 清除选中状态
            mProblemTypeAdapter.setSelectedList(null)
            mSubjectAdapter.setSelectedList(null)
            mQuestionTypeAdapter.setSelectedList(null)
            mDateAdapter.setSelectedList(null)

            RxBus.get().send(STATUS_SUBJECT, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_TYPE, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_QUESTION_TYPE, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_DATE, FilterData(-1, "默认"))
        }

        main_drawer_btn_confirm.setOnClickListener {
            main_drawer_layout.closeDrawer(Gravity.END)
            RxBus.get().send(STATUS_SUBJECT, FilterData.subjectAll[mSubjectPos])
            RxBus.get().send(STATUS_TYPE, FilterData.jobType[mTypePos])
            RxBus.get().send(STATUS_QUESTION_TYPE, FilterData.subjectAll[mQuestionTypePos])
            RxBus.get().send(STATUS_DATE, FilterData.jobType[mDatePos])
        }

    }

    /**
     * 侧滑菜单事件
     */
    fun switchDrawerLayout() {
        if (main_drawer_layout.isDrawerOpen(Gravity.END))
            main_drawer_layout.closeDrawer(Gravity.END)
        else
            main_drawer_layout.openDrawer(Gravity.END)
    }

    fun disableDrawerLayout() {
        main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun enableDrawerLayout() {
        main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    /**
     * 根据不同Item 展现不同菜单
     */
    companion object {
        const val WORK = 0x333
        const val VIDEO = 0x222
    }

    fun filterLayoutItem(pos: Int, item: Int) {
        if (item == WORK) {
            mProblemTypeAdapter.setSelectedList(null)
            mSubjectAdapter.setSelectedList(null)
            mQuestionTypeAdapter.setSelectedList(null)
            mDateAdapter.setSelectedList(null)
            when (pos) {
                0 -> {
                    main_tv_subject.visibility = View.VISIBLE
                    subject_layout.visibility = View.VISIBLE
                    main_tv_type.visibility = View.VISIBLE
                    type_layout.visibility = View.VISIBLE
                    main_tv_question_type.visibility = View.GONE
                    question_type_layout.visibility = View.GONE
                    main_tv_date.visibility = View.GONE
                    date_layout.visibility = View.GONE
                }
                1 -> {
                    main_tv_subject.visibility = View.GONE
                    subject_layout.visibility = View.GONE
                    main_tv_type.visibility = View.VISIBLE
                    type_layout.visibility = View.VISIBLE
                    main_tv_question_type.visibility = View.GONE
                    question_type_layout.visibility = View.GONE
                    main_tv_date.visibility = View.GONE
                    date_layout.visibility = View.GONE
                }
                2 -> {
                    main_tv_subject.visibility = View.VISIBLE
                    subject_layout.visibility = View.VISIBLE
                    main_tv_type.visibility = View.GONE
                    type_layout.visibility = View.GONE
                    main_tv_question_type.visibility = View.VISIBLE
                    question_type_layout.visibility = View.VISIBLE
                    main_tv_date.visibility = View.VISIBLE
                    date_layout.visibility = View.VISIBLE
                }
            }
        }

        if (item == VIDEO) {
            when (pos) {
                0 -> {
                }
                1 -> {
                }
                2 -> {
                }
            }
        }
    }

}
