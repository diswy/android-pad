package com.cqebd.student.ui

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.db.dao.Attachment
import com.cqebd.student.presenter.AnswerPresenter
import com.cqebd.student.tools.AudioPlayer
import com.cqebd.student.tools.toast
import com.cqebd.student.views.IAnswer
import com.cqebd.student.widget.AnswerCardView1
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_answer.*

/**
 * 描述
 * Created by gorden on 2018/3/21.
 */
class AnswerActivity:BaseActivity(),IAnswer {

    private lateinit var audioPlayer:AudioPlayer
    private lateinit var presenter:AnswerPresenter
    private lateinit var behaviorItem: BottomSheetBehavior<*>
    private lateinit var behaviorCard: BottomSheetBehavior<*>
    private var submitMode = 0
    private lateinit var menuWindow: PopupWindow

    override fun setContentView() {
        setContentView(R.layout.activity_answer)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        submitMode = intent.getIntExtra("submitMode", 0)
        audioPlayer = AudioPlayer(btn_play, progressBar, text_progress)
        initCardViewEvent()
        presenter = AnswerPresenter(this)
        presenter.bindData()
        initMoreMenu()
        audioPlayer.setOnCompletionListener { mp -> presenter.answerCardState() }
    }

    //初始答题卡
    private fun initCardViewEvent() {

        behaviorCard = BottomSheetBehavior.from(cardView)
        behaviorItem = BottomSheetBehavior.from(recyclerItem)
        behaviorCard.state = BottomSheetBehavior.STATE_HIDDEN
        behaviorItem.state = BottomSheetBehavior.STATE_HIDDEN

        behaviorCard.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    handlebg.setImageResource(R.drawable.icon_answer_handle_open)
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    handlebg.setImageResource(R.drawable.icon_answer_handle_close)
                    cardView.hideSoftKeyBord()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private fun initMoreMenu() {
        val menuView = LayoutInflater.from(this).inflate(R.layout.menu_more, null)
        menuWindow = PopupWindow(menuView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        menuWindow.isFocusable = true
        menuWindow.isOutsideTouchable = true
        menuWindow.setBackgroundDrawable(BitmapDrawable())

        menuView.findViewById<View>(R.id.text_jsq).setOnClickListener {
            openJS()
            menuWindow.dismiss()
        }
        menuView.findViewById<View>(R.id.text_cgz).setOnClickListener {
            startActivity<DraftPaperActivity>()
            menuWindow.dismiss()
        }
    }

    /**
     * 打开计算器
     */
    fun openJS() {
        val pak = getAllApps(this, "Calculator", "calculator") //大小写
        if (pak != null) {
            val intent = this.packageManager.getLaunchIntentForPackage(pak.packageName)
            startActivity(intent)
        } else {
            toast("未找到计算器")
        }
    }

    fun getAllApps(context: Context, app_flag_1: String, app_flag_2: String): PackageInfo? {
        val pManager = context.packageManager
        // 获取手机内所有应用
        val packlist = pManager.getInstalledPackages(0)
        for (i in packlist.indices) {
            val pak = packlist[i] as PackageInfo
            if (pak.packageName.contains(app_flag_1) || pak.packageName.contains(app_flag_2)) {
                return pak
            }
        }
        return null
    }

    override fun bindEvents() {
        view_submit.setOnClickListener {
            presenter.showCommitDialog()
        }
        handlebg.setOnClickListener {
            toggleBehavior(behaviorCard)
        }
        view_location.setOnClickListener {
            behaviorCard.state = BottomSheetBehavior.STATE_HIDDEN
            toggleBehavior(behaviorItem)
        }
        btn_more.setOnClickListener {
            menuWindow.showAsDropDown(btn_more, 0, 0)
        }
    }

    override fun viewPager(): ViewPager {
        return pagerContent
    }

    override fun recyclerView(): RecyclerView {
        return recyclerItem
    }

    override fun answerCardView(): AnswerCardView1 {
        return cardView
    }

    override fun setTaskName(name: String?) {
        text_title.text = name
    }

    override fun setLoacation(location: String?) {
        text_location.text = location
    }

    override fun setCountDown(time: String?) {
        text_time.text = time
    }

    override fun closeBehavior() {
        behaviorCard.setState(BottomSheetBehavior.STATE_HIDDEN)
        behaviorItem.setState(BottomSheetBehavior.STATE_HIDDEN)
    }

    override fun prohibitAnswer() {
        behaviorCard.setState(BottomSheetBehavior.STATE_HIDDEN)
        cardView.visibility = View.GONE
        handlebg.visibility = View.GONE
    }

    override fun allowAnswer() {
        cardView.visibility = View.VISIBLE
        handlebg.visibility = View.VISIBLE
    }

    override fun tipMessage(msg: String?) {
        if (msg == null) {
            text_tips.visibility = View.GONE
        } else {
            text_tips.text = msg
            text_tips.visibility = View.VISIBLE
        }
    }

    override fun audioInfo(attachment: Attachment?) {
        if (attachment == null) {
            audioPlayer.release()
            lin_audio.visibility = View.GONE
        } else {
            lin_audio.visibility = View.VISIBLE
            audioPlayer.openAudio(attachment)
        }
    }

    fun answerCardState() {
        presenter.answerCardState()
    }

    override fun submitMode(): Int {
        return submitMode
    }

    private fun toggleBehavior(behavior: BottomSheetBehavior<*>) {
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.onDestroy()
    }
}