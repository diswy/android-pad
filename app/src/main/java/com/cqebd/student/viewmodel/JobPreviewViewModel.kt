package com.cqebd.student.viewmodel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.cqebd.student.app.App
import com.cqebd.student.constant.Constant
import com.cqebd.student.db.dao.AttachmentDao
import com.cqebd.student.http.NetApi
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toastError
import com.cqebd.student.ui.AnswerActivity
import com.cqebd.student.ui.AttachmentActivity
import com.cqebd.student.vo.entity.BaseBean
import com.cqebd.student.vo.entity.QuestionGroupInfo
import com.cqebd.student.vo.entity.QuestionInfo
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.student.widget.LoadingDialog
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import java.util.*

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
class JobPreviewViewModel(private val info: WorkInfo):ViewModel() {
    private val attachmentDao by lazy { App.getDaoSession().attachmentDao }
    private var previewPromptFormat = "此为计时%s分钟限时答题，中途不能退出 ，是否答题"
    private val loadingDialog by lazy { LoadingDialog() }

    /**
     * 开始答题
     * @param skipAttachment 是否跳过检查附件
     */
    fun startAnswer(activity: AppCompatActivity,skipAttachment:Boolean = false){
        if (info.attachments!=null&& info.attachments.isNotEmpty()&&!skipAttachment){
            info.attachments.forEach {
                val attachment = attachmentDao.queryBuilder().where(AttachmentDao.Properties.Id.eq(it.examinationPapersId.toString().plus(loginId))).build().unique()
                val count = attachment?.watchCount?:0

                if (it.canWatchTimes==0||count<it.canWatchTimes){
                    activity.startActivity<AttachmentActivity>("papers" to true,
                            "taskInfo" to info,"attachment" to info.attachments)
                    activity.finish()
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
                    .setOnDismissListener { endWork() }
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
                                val intent = Intent(activity,AnswerActivity::class.java)
                                intent.putExtra("submitMode",submitMode)
                                putWorkData(intent, it.body!![0].QuestionGruop, info)
                                activity.startActivity(intent)
                                activity.finish()
                                RxBus.get().send(Constant.BUS_WORKTASK_CHANGE)
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
    private fun endWork(){
        com.cqebd.student.http.NetClient.createApi(NetApi::class.java).endWork(info.TaskId.toInt())
                .enqueue(object :NetCallBack<BaseBean>(){
                    override fun onSucceed(response: BaseBean?) {
                        RxBus.get().send(Constant.BUS_WORKTASK_CHANGE)
                        RxBus.get().send(Constant.BUS_FINISH_PREVIEW)
                    }

                    override fun onFailure() {
                    }
        })
    }

    fun putWorkData(data: Intent,
                    questionGroupInfos: List<QuestionGroupInfo>,
                    taskInfo: WorkInfo): Intent {
        val infos = ArrayList<QuestionInfo>()
        for (groupInfo in questionGroupInfos) {
            infos.addAll(groupInfo.getQuestion())
        }
        data.putExtra(Constant.TASK_INFO, taskInfo)
        data.putParcelableArrayListExtra(Constant.QUESTION_INFO, infos as ArrayList<out Parcelable>)
        return data
    }

    class Factory(private val info: WorkInfo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(WorkInfo::class.java)
                    .newInstance(info)
        }
    }
}