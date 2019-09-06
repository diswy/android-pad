package com.cqebd.student.ui.card

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.tai.PrivateInfo
import com.cqebd.student.tai.TAIAudioPlayer
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.ui.AnswerActivity
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer
import com.cqebd.student.widget.AnswerCardView1
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.tencent.taisdk.*
import com.xiaofu.lib_base_xiaofu.cache.ACache
import com.xiaofu.lib_base_xiaofu.fancy.FancyDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_en_tai_answer.*
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class EnTaiAnswerFragment : BaseFragment() {

//    private val oral = TAIOralEvaluation()
    private lateinit var oral: TAIOralEvaluation

    private var timeDisposable: Disposable? = null
    private var changeListener: DataChangeListener? = null
    private val audioPlayer = TAIAudioPlayer()

    private var currentAudioUrl = ""
    private var isPlaying = false
    private val timeFormat = "%d.%d%d"
    private lateinit var mCache: ACache

    private var viewVisible = true

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_en_tai_answer, container, false)
    }

    override fun onDestroyView() {
        viewVisible = false
        loadingDisposable?.dispose()
        oralRelease()
        stopTime()
        audioPlayer.release()
        super.onDestroyView()
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }

    fun build(type: AnswerType, mode: Int, studentAnswer: StudentAnswer?, subject: String) {
        try {
            oral = (activity as AnswerActivity).getTAIOral()
            Log.e("xiaofu", "------>>>>>>oral = $oral")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("xiaofu", "try catch错误，错误内容${e.message}")
        }

        mCache = ACache.get(requireActivity())

        Log.w("xiaofu", ">>>>>>语音内容：$subject")
        Log.w("xiaofu", ">>>>>>答案：${Gson().toJson(studentAnswer)}")

        val answer = studentAnswer?.Answer
        if (!TextUtils.isEmpty(answer)) {
            try {
                val data: TAIOralEvaluationRet = Gson().fromJson(answer, TAIOralEvaluationRet::class.java)
                if (!TextUtils.isEmpty(data.audioUrl)) {
                    currentAudioUrl = data.audioUrl
                    tvBtnReplay.isEnabled = true
                    tvBtnRecord.text = "重录"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        tvBtnRecord.setOnClickListener {
            startRecord(type, subject, mode)
        }

        tvBtnReplay.setOnClickListener {
            if (isPlaying) {
                stopAnim()
                tvBtnRecord.isEnabled = true
                stopTime()
                audioPlayer.release()
                tvTime.text = "点击可回放"
            } else {
                tvTime.text = "准备中..."
                tvBtnRecord.isEnabled = false
                audioPlayer.openAudio(currentAudioUrl)
            }
            isPlaying = !isPlaying
        }

        audioPlayer.setOnStartListener {
            startAnim()
            stopTime()
            timeDisposable = startTime()
        }

        audioPlayer.setOnCompleteListener {
            stopAnim()
            tvBtnRecord.isEnabled = true
            stopTime()
            audioPlayer.release()
            tvTime.text = "点击可回放"
            isPlaying = false
        }
    }


    private fun initTAIParam(content: String, mode: Int): TAIOralEvaluationParam {
        val param = TAIOralEvaluationParam()
        param.context = requireContext()
        param.sessionId = UUID.randomUUID().toString()
        param.appId = PrivateInfo.appId
        param.soeAppId = PrivateInfo.soeAppId
        param.secretId = PrivateInfo.secretId
        param.secretKey = PrivateInfo.secretKey
        param.token = PrivateInfo.token

        param.workMode = TAIOralEvaluationWorkMode.STREAM
        param.storageMode = TAIOralEvaluationStorageMode.ENABLE
        param.fileType = TAIOralEvaluationFileType.MP3
        param.serverType = TAIOralEvaluationServerType.ENGLISH
        param.textMode = TAIOralEvaluationTextMode.NORMAL

        val score = mCache.getAsString("EnglishScoreCoeff")
        val scoreF = try {
            score.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            3.0
        }

        param.scoreCoeff = scoreF// 苛刻指数

        when (mode) {
            AnswerCardView1.TYPE_EN_WORD -> {// 单词
                param.evalMode = TAIOralEvaluationEvalMode.WORD
                param.refText = content
            }
            AnswerCardView1.TYPE_EN_SENTENCE -> {// 句子
                param.evalMode = TAIOralEvaluationEvalMode.SENTENCE
                param.refText = content
            }
            AnswerCardView1.TYPE_EN_PARAGRAPH -> {//段落
                param.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH
                param.refText = content
            }
            AnswerCardView1.TYPE_EN_FREE -> {// 自由
                param.evalMode = TAIOralEvaluationEvalMode.FREE
            }
        }

        return param
    }

    private fun startRecord(type: AnswerType, subject: String, mode: Int) {
        if (oral.isRecording) {// 录音中先关闭就好了
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_btn_record)
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            tvBtnRecord.setCompoundDrawables(null, drawable, null, null)
            showDialog()
            stopAnim()
            tvTime.text = "点击右侧按钮即可回放"
            tvBtnRecord.text = "重录"
            tvBtnRecord.textColor = Color.parseColor("#bababa")
            tvBtnReplay.isEnabled = true
            stopTime()
            oral.stopRecordAndEvaluation {

            }
        } else {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_btn_record_stop)
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            tvBtnRecord.setCompoundDrawables(null, drawable, null, null)
            startAnim()
            tvBtnRecord.text = "停止"
            tvBtnRecord.textColor = Color.parseColor("#e63641")
            tvBtnReplay.isEnabled = false
            tvTime.text = "准备中..."
            oral.setListener(object : TAIOralEvaluationListener {
                // 结果监听
                override fun onVolumeChanged(volume: Int) {

                }

                override fun onEvaluationData(data: TAIOralEvaluationData, result: TAIOralEvaluationRet?, error: TAIError?) {
                    val gson = Gson()
                    val errString = gson.toJson(error)
                    val retString = gson.toJson(result)
                    Log.e("xiaofu", String.format("oralEvaluation:seq:%d, end:%d, error:%s, ret:%s", data.seqId, if (data.bEnd) 1 else 0, errString, retString))

                    if (error != null && error.code != 0) {// 发生错误
                        onUiThread {
                            toast(error.desc+"，请重新录制")
                            stopTime()
                            stopAnim()
                            val mDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_btn_record)
                            mDrawable?.apply {
                                setBounds(0, 0, minimumWidth, minimumHeight)
                            }
                            tvBtnRecord.setCompoundDrawables(null, mDrawable, null, null)
                            oral.stopRecordAndEvaluation {

                            }
                            tvTime.text = "点击麦克风开始录音"
                            tvBtnRecord.text = "录音"
                            tvBtnRecord.textColor = Color.parseColor("#bababa")
                        }
                    }

                    if (data.bEnd && viewVisible && result != null) {
                        val studentAnswer = StudentAnswer()
                        studentAnswer.Id = type.id
                        studentAnswer.TypeId = type.typeId
                        studentAnswer.Answer = retString
                        changeListener?.onDataChanged(studentAnswer)
                        currentAudioUrl = result.audioUrl
                        try {
                            if (!TextUtils.isEmpty(currentAudioUrl)) {
                                onUiThread {
                                    tvBtnReplay.isEnabled = true
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onEndOfSpeech() {
                }

            })

            val recordParam = TAIRecorderParam()
            recordParam.fragSize = 1024
            recordParam.vadEnable = true
            recordParam.vadInterval = 3000
            oral.setRecorderParam(recordParam)
            oral.startRecordAndEvaluation(initTAIParam(subject, mode)) {

                stopTime()
                timeDisposable = startTime()

                val s = Gson().toJson(it)
                Logger.d("--->>>答题卡开始录音：$s")
            }
        }
    }

    private fun startTime(): Disposable {
        var timeCount = 0
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    timeCount++
                    val s1 = timeCount / 60
                    val s2 = timeCount % 60 / 10
                    val s3 = timeCount % 10
                    tvTime.text = timeFormat.format(s1, s2, s3)
                }
    }

    private fun stopTime() {
        timeDisposable?.dispose()
    }

    private var leftAnim: AnimationDrawable? = null
    private var rightAnim: AnimationDrawable? = null

    private fun startAnim() {
        try {
            leftAnim = ivLeftAnim.drawable as AnimationDrawable
            rightAnim = ivRightAnim.drawable as AnimationDrawable

            leftAnim?.start()
            rightAnim?.start()

            ivLeftAnim.visibility = View.VISIBLE
            ivRightAnim.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAnim() {
        ivLeftAnim.visibility = View.GONE
        ivRightAnim.visibility = View.GONE
        leftAnim?.stop()
        rightAnim?.stop()
    }

    private fun oralRelease() {
        oral.stopRecordAndEvaluation {}
    }

    private var loadingDialog: FancyDialogFragment? = null
    private var loadingDisposable: Disposable? = null
    private fun showDialog() {
        loadingDialog = FancyDialogFragment.create()
        loadingDialog!!.isCancelable = false
        loadingDialog!!.isCancelable = false
        loadingDialog!!.setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_wait_loading)
                .setWidth(requireContext(), 260)
                .setViewListener { dialog, v ->

                }
        loadingDialog!!.show(requireActivity().fragmentManager, "")


        loadingDisposable = RxCounter.tick(1).doOnComplete {
            loadingDialog?.dismiss()
        }.subscribe()
    }


}
