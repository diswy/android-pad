package com.cqebd.student

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.event.*
import com.cqebd.student.netease.NetEaseCache
import com.cqebd.student.tools.*
import com.cqebd.student.tools.string.MD5
import com.cqebd.student.ui.HomeFragment
import com.cqebd.student.ui.MineFragment
import com.cqebd.student.ui.root.HomeworkFragment
import com.cqebd.student.ui.root.RootHomeFragment
import com.cqebd.student.ui.root.RootVideoFragment
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.UserAccount
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.orhanobut.logger.Logger
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import gorden.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawerlayout.*

class MainActivity : BaseActivity() {
    private var currentFragment: Fragment? = null
    private val mGuidePosition by lazy { intent.getIntExtra("guide_position", -1) }

    override fun setContentView() {
        setContentView(R.layout.activity_main_drawerlayout)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        RxBus.get().register(this)
        loginNetease()
        initDrawerView()

        val titles = resources.getStringArray(R.array.title)
        navigation.addItem(BottomNavigationItem(R.drawable.ic_home_selected, titles[0]).setInactiveIconResource(R.drawable.ic_home_normal))
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
        switchFragment(if (mGuidePosition != -1) mGuidePosition else 0)
        navigation.selectTab(if (mGuidePosition != -1) mGuidePosition else 0)
    }

    private fun switchFragment(position: Int) {
        var targetFragment: Fragment? = supportFragmentManager.findFragmentByTag("tag$position")
        if (targetFragment == null) {
            when (position) {
                0 -> {
//                    targetFragment = HomeFragment()
                    targetFragment = RootHomeFragment()
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

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出点点课")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    /**
     * -------------------侧滑菜单部分-------------------
     */
    private var mSubjectPos = 0     // 学科
    private var mTypePos = 0        // 类型
    private var mQuestionTypePos = 0// 题型
    private var mDatePos = 0        // 日期
    private var mTimePos = 0        // 时段
    private var mSubscribePos = 0   // 订阅状态
    private lateinit var mProblemTypeAdapter: TagAdapter<FilterData>
    private lateinit var mSubjectAdapter: TagAdapter<FilterData>
    private lateinit var mQuestionTypeAdapter: TagAdapter<FilterData>
    private lateinit var mDateAdapter: TagAdapter<FilterData>
    private lateinit var mTimeAdapter: TagAdapter<FilterData>
    private lateinit var mSubscribeAdapter: TagAdapter<FilterData>
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
        // 时段适配
        mTimeAdapter = object : TagAdapter<FilterData>(FilterData.dateTime) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        time_layout.adapter = mTimeAdapter
        time_layout.setOnTagClickListener { _, position, _ ->
            mTimePos = position
            if (mTimePos == position) {
                mTimeAdapter.setSelectedList(position)
            }
            return@setOnTagClickListener true
        }
        // 订阅状态适配
        mSubscribeAdapter = object : TagAdapter<FilterData>(FilterData.subscribeStatus) {
            override fun getView(parent: FlowLayout?, position: Int, data: FilterData?): View {
                val tv = mInflater.inflate(R.layout.tag_tv, parent, false) as TextView
                tv.text = data?.Name
                return tv
            }
        }
        subscribe_layout.adapter = mSubscribeAdapter
        subscribe_layout.setOnTagClickListener { _, position, _ ->
            mSubscribePos = position
            if (mSubscribePos == position) {
                mSubscribeAdapter.setSelectedList(position)
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
            mTimeAdapter.setSelectedList(null)
            mSubscribeAdapter.setSelectedList(null)

            RxBus.get().send(STATUS_SUBJECT, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_TYPE, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_QUESTION_TYPE, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_DATE, FilterData(-1, "默认"))

            RxBus.get().send(STATUS_TIME, FilterData(-1, "默认"))
            RxBus.get().send(STATUS_SUBSCRIBE, FilterData(-1, "默认"))

        }

        main_drawer_btn_confirm.setOnClickListener {
            main_drawer_layout.closeDrawer(Gravity.END)
            RxBus.get().send(STATUS_SUBJECT, FilterData.subjectAll[mSubjectPos])
            RxBus.get().send(STATUS_TYPE, FilterData.jobType[mTypePos])
            RxBus.get().send(STATUS_QUESTION_TYPE, FilterData.subjectAll[mQuestionTypePos])
            RxBus.get().send(STATUS_DATE, FilterData.jobType[mDatePos])

            RxBus.get().send(STATUS_TIME, FilterData.dateTime[mTimePos])
            RxBus.get().send(STATUS_SUBSCRIBE, FilterData.subscribeStatus[mSubscribePos])
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

            main_tv_time.visibility = View.GONE
            main_tv_subscribe.visibility = View.GONE
            subscribe_layout.visibility = View.GONE
            time_layout.visibility = View.GONE
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
            main_tv_type.visibility = View.GONE
            type_layout.visibility = View.GONE
            main_tv_question_type.visibility = View.GONE
            question_type_layout.visibility = View.GONE
            main_tv_date.visibility = View.GONE
            date_layout.visibility = View.GONE

            when (pos) {
                0 -> {
                    main_tv_subject.visibility = View.VISIBLE
                    subject_layout.visibility = View.VISIBLE
                    main_tv_time.visibility = View.VISIBLE
                    main_tv_subscribe.visibility = View.VISIBLE
                    subscribe_layout.visibility = View.VISIBLE
                    time_layout.visibility = View.VISIBLE
                }
            }
        }
    }

    private var loginRequest: AbortableFuture<LoginInfo>? = null
    private fun loginNetease() {
//        if (isNeteaseLogin()){
//            NetEaseCache.setAccount(getNeteaseLoginInfo()?.account)
//            return
//        }
        UserAccount.load()?.let {
            if (!TextUtils.isEmpty(password) && isLogin()) {
                val mAccount = "student_${it.ID}"
                val token = MD5.getStringMD5(password)
                loginRequest = NIMClient.getService(AuthService::class.java).login(LoginInfo(mAccount, token))
                loginRequest?.setCallback(object : RequestCallback<LoginInfo> {
                    override fun onSuccess(param: LoginInfo) {
                        Logger.d("网易云信:account = ${param.account} ; psd = ${param.token} ; appKey = ${param.appKey} ")
                        loginRequest = null
                        NetEaseCache.setContext(this@MainActivity)
                        NetEaseCache.setAccount(mAccount)
                        saveNetease(mAccount, token)
                    }

                    override fun onFailed(code: Int) {
                        if (code == 302 || code == 404) {
                            Logger.e("网易云信:账号或密码错误")
                        } else {
                            Logger.e("网易云信:登录失败: $code")
                        }
                    }

                    override fun onException(exception: Throwable) {
                        Logger.e("网易云信:", exception)
                    }
                })
            }
        }
    }

}
