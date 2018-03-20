package com.cqebd.student.viewmodel

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.cqebd.student.MainActivity
import com.cqebd.student.db.ExDataBase
import com.cqebd.student.db.dao.AttachmentDao
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toastError
import com.cqebd.student.ui.VideoActivity
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.student.widget.LoadingDialog
import gorden.lib.anko.static.startActivity

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
class JobPreviewViewModel(private val info: WorkInfo):ViewModel() {
    private var previewPromptFormat = "此为计时%s分钟限时答题，中途不能退出 ，是否答题"
    private val loadingDialog by lazy { LoadingDialog() }
    private val attachmentDao: AttachmentDao by lazy { ExDataBase.getInstance().attachmentDao() }

    /**
     * 开始答题
     * @param skipAttachment 是否跳过检查附件
     */
    fun startAnswer(activity: AppCompatActivity,skipAttachment:Boolean = false){
        if (info.attachments!=null&& info.attachments.isNotEmpty()&&!skipAttachment){
            info.attachments.forEach {
                val attachment = attachmentDao.queryAttachment(info.PapersId.toString().plus(loginId))
                val count = attachment?.watchCount ?: 0
                if (it.canWatchTimes==0||count<it.canWatchTimes){
                    activity.startActivity<VideoActivity>("papers" to true,
                            "taskInfo" to info)
                    return
                }
            }
        }
        if (info.Duration==0&&info.IsTasks){
            startAnswerWork(activity)
        }else if (info.IsTasks){
            AlertDialog.Builder(activity).setMessage(previewPromptFormat.format(info.Duration))
                    .setPositiveButton("是",{_,_->
                        startAnswerWork(activity)
                    })
                    .setNegativeButton("否",null)
                    .show()
        }else{
            AlertDialog.Builder(activity).setMessage("答题时间已结束,将为你自动交卷")
                    .setPositiveButton("知道了",null)
                    .setOnDismissListener { endWork(activity) }
                    .show()
        }

    }

    private fun startAnswerWork(activity: AppCompatActivity){
        loadingDialog.show(activity.supportFragmentManager)
        NetClient.workService().startWork(info.TaskId).observe(activity, Observer {
            if (it?.isSuccessful()==true){
                val startDate = it.body?.get("startDate")?.asString
                val submitMode = it.body?.get("submitMode")?.asInt
                info.StartTime = startDate
                NetClient.workService().getExaminationPaper(info.PapersId,info.TaskId)
                        .observe(activity, Observer {
                            if (it?.isSuccessful()==true){
                                loadingDialog.dismiss()
//                                activity.startActivity<MainActivity>("submitMode" to submitMode)
                            }else{
                                loadingDialog.dismiss()
                                toastError(it?.errorMessage)
                            }
                        })

            }else{
                loadingDialog.dismiss()
                toastError(it?.errorMessage)
            }
        })

    }
    fun endWork(activity: Activity){

    }

    class Factory(private val info: WorkInfo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(WorkInfo::class.java)
                    .newInstance(info)
        }
    }
}