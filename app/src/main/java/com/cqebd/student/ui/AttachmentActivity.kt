package com.cqebd.student.ui

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.GridLayout
import com.anko.static.appWidth
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.db.dao.AttachmentDao
import com.cqebd.student.tools.loginId
import com.cqebd.student.viewmodel.JobPreviewViewModel
import com.cqebd.student.vo.entity.Attachment
import com.cqebd.student.vo.entity.WorkInfo
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.logError
import gorden.rxbus.RxBus
import gorden.widget.selector.SelectorButton
import kotlinx.android.synthetic.main.activity_attachment.*
import java.util.*

/**
 * 描述
 * Created by gorden on 2018/3/21.
 */
class AttachmentActivity : BaseActivity() {
    private var taskInfo: WorkInfo? = null
    private lateinit var currentAttachment: com.cqebd.student.db.dao.Attachment
    private val watchDataList = ArrayList<com.cqebd.student.db.dao.Attachment>()
    private val attachmentDao = App.getDaoSession().attachmentDao
    private val viewModel: JobPreviewViewModel by lazy { ViewModelProviders.of(this, JobPreviewViewModel.Factory(taskInfo!!)).get(JobPreviewViewModel::class.java) }
    private var papers: Boolean = false
    override fun setContentView() {
        setContentView(R.layout.activity_attachment)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        RxBus.get().register(this)
        val taskId = intent.getIntExtra("taskId", -1)
        papers = intent.getBooleanExtra("papers", false)
        val attachmentList = intent.getParcelableArrayListExtra<Attachment>("attachment")

        if (papers) {
            text_info.visibility = View.VISIBLE
            btn_startAnswer.visibility = View.VISIBLE

            val info = StringBuilder()
            for (attachment in attachmentList) {
//                XLog.e("id  =   " + attachment.id + "   " + attachment.examinationPapersId)
                info.append(attachment.remarks)
            }
//            text_info.text = info.toString()
            text_info.text = Html.fromHtml(info.toString())
            taskInfo = intent.getParcelableExtra("taskInfo")
        }


        attachmentList.forEach {
            var id = taskId.toString().plus(it.id)
            if (papers) {
                id = it.examinationPapersId.toString().plus(it.id).plus(loginId)
            }
            var watchData = attachmentDao.queryBuilder().where(AttachmentDao.Properties.Id.eq(id)).build().unique()
            if (watchData == null) {
                watchData = com.cqebd.student.db.dao.Attachment()
            }
            watchData.id = id
            watchData.taskId = taskId
            watchData.url = it.url
            watchData.name = it.name
            watchData.answerType = it.answerType
            watchData.canWatchCount = it.canWatchTimes
            watchDataList.add(watchData)
        }
        if (papers) startEnable()

        currentAttachment = watchDataList[0]
        videoView.canSeek = (currentAttachment.answerType == 1 && currentAttachment.canWatchCount == 0) ||//播放中可答、可无限次观看
                (currentAttachment.answerType == 2 && currentAttachment.watchCount > 0 && currentAttachment.canWatchCount == 0)//播放完可答、可无限次观看、已经观看一次
        videoView.saveProgress = true to currentAttachment.id
        videoView.setVideoPath(currentAttachment.url, currentAttachment.name, R.drawable.ic_login_logo)

        if (watchDataList.size > 1) {
            val width = (appWidth - 30.dp) / 2
            for (attachment in watchDataList) {
                val button = SelectorButton(this)
                button.setTextColor(Color.WHITE)
                button.s_solid_color = ContextCompat.getColor(this, R.color.btn_green)
                button.s_solid_pressed_color = ContextCompat.getColor(this, R.color.btn_green_press)
                button.setSingleLine()
                button.setPadding(20, 0, 20, 0)
                button.ellipsize = TextUtils.TruncateAt.END
                button.s_radius = 5.dp.toFloat()
                button.text = attachment.name
                gridLayout.addView(button, width, 30.dp)
                (button.layoutParams as GridLayout.LayoutParams).leftMargin = 5.dp
                (button.layoutParams as GridLayout.LayoutParams).topMargin = 5.dp
                (button.layoutParams as GridLayout.LayoutParams).rightMargin = 5.dp

                button.setOnClickListener {
                    currentAttachment = attachment
                    videoView.canSeek = (currentAttachment.answerType == 1 && currentAttachment.canWatchCount == 0) ||//播放中可答、可无限次观看
                            (currentAttachment.answerType == 2 && currentAttachment.watchCount > 0 && currentAttachment.canWatchCount == 0)//播放完可答、可无限次观看、已经观看一次

                    videoView.saveProgress = true to currentAttachment.id
                    videoView.setVideoPath(currentAttachment.url, currentAttachment.name, R.drawable.ic_login_logo)
                    Logger.d(currentAttachment.url)
                }
            }
        } else {
            text_see.visibility = View.GONE
        }
    }

    override fun bindEvents() {
        videoView.setOnCompletionListener {
            logError("播放完成")
            currentAttachment.watchCount = currentAttachment.watchCount + 1
            attachmentDao.insertOrReplace(currentAttachment)
            if (papers) startEnable()
        }

        videoView.setOnInterceptPlayingListener {
            if (currentAttachment.canWatchCount in 1..currentAttachment.watchCount){
                AlertDialog.Builder(this)
                        .setMessage("你最多只能观看该视频 ${currentAttachment.canWatchCount} 次")
                        .setPositiveButton("OK", null).show()
                return@setOnInterceptPlayingListener true
            }
            false
        }

        btn_startAnswer.setOnClickListener {
            this.finish()
            viewModel.startAnswer(this, true)
        }
    }

    override fun onStop() {
        super.onStop()
        videoView.onStop()
    }

    override fun onBackPressed() {
        if (videoView.onBackPressed())
            return
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unRegister(this)
    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
    }

    private fun startEnable() {
        for (attachment in watchDataList) {
            logError("观看信息  " + attachment.answerType + "  " + attachment.watchCount)
            if (attachment.answerType == 2 && attachment.watchCount == 0) {
                btn_startAnswer.isEnabled = false
                return
            }
        }
        btn_startAnswer.isEnabled = true
    }
}