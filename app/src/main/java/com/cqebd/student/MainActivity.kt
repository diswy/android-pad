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
import com.cqebd.student.event.*
import com.cqebd.student.ui.*
import com.cqebd.student.ui.root.HomeworkFragment
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
                    targetFragment = VideoFragment()
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

    override fun bindEvents() {
    }

    /**
     * 侧滑菜单处理
     */
    fun switchDrawerLayout(type: Int) {
        when (type) {
            HomeworkFragment.HOMEWORK -> {
                main_tv_subject.visibility = View.VISIBLE
                subject_layout.visibility = View.VISIBLE
            }
            HomeworkFragment.WRONG_WORK -> {
                main_tv_subject.visibility = View.GONE
                subject_layout.visibility = View.GONE
            }
        }


        if (main_drawer_layout.isDrawerOpen(Gravity.END))
            main_drawer_layout.closeDrawer(Gravity.END)
        else
            main_drawer_layout.openDrawer(Gravity.END)
    }


    private var mSubjectPos = 0
    private var mTypePos = 0
    private fun initDrawerView() {
        val mInflater = LayoutInflater.from(this)
        // 禁用手势侧滑
        main_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        type_layout.adapter = object : TagAdapter<FilterData>(FilterData.problemType) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }

        val mSubjectAdapter = object : TagAdapter<FilterData>(FilterData.subjectAll) {
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



        main_drawer_btn_clear.setOnClickListener {
            RxBus.get().send(STATUS_SUBJECT, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_TYPE, FilterData(-1, "默认"))
        }

        main_drawer_btn_confirm.setOnClickListener {
            RxBus.get().send(STATUS_SUBJECT, FilterData.subject[mSubjectPos])
            RxBus.get().send(STATUS_TYPE, FilterData.problemType[mTypePos])
        }

//        ###事件
//
//        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
//        {
//            @Override
//            public boolean onTagClick(View view, int position, FlowLayout parent)
//            {
//                Toast.makeText(getActivity(), mVals[position], Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//
//        点击标签时的回调。
//
//        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener()
//        {
//            @Override
//            public void onSelected(Set<Integer> selectPosSet)
//            {
//                getActivity().setTitle("choose:" + selectPosSet.toString());
//            }
//        });
    }
}
