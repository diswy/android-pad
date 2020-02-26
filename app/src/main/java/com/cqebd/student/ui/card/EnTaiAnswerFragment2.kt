package com.cqebd.student.ui.card


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.tai.PrivateInfo
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.AnswerActivity
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.Attachment
import com.cqebd.student.vo.entity.StudentAnswer
import com.cqebd.student.widget.AnswerCardView1
import com.google.gson.Gson
import com.tencent.taisdk.*
import com.xiaofu.lib_base_xiaofu.cache.ACache
import gorden.lib.video.ExAudioPlayer
import kotlinx.android.synthetic.main.fragment_en_tai_answer_fragment2.*
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference
import java.util.*

/**
 * 改版口语评测
 */
class EnTaiAnswerFragment2 : BaseFragment() {
    private val completionFormat = "发音完整度：%.2f"
    private val fluencyFormat = "发音流利度：%.2f"
    private val accuracyFormat = "发音准确度：%.2f"
    private val suggestScore = "%.2f"

    private lateinit var oral: TAIOralEvaluation
    private lateinit var exoPlayer: ExAudioPlayer
    private lateinit var mCache: ACache// 获取评分苛刻程度
    private var currentAudioUrl = ""// 当前存在的学生录制过的音频
    private var changeListener: DataChangeListener? = null
    private var mOriginVoiceUrl: String = ""
    private var isPlaying = false// 本地音频播放器播放标记

    // 信息接收初始化
    private lateinit var mType: AnswerType
    private var mMode: Int = 0
    private var mSubject: String = ""


    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_en_tai_answer_fragment2, container, false)
    }

    override fun onDestroyView() {
        exoPlayer.setOnCompletionListener(null)
        oral.setListener(null)
        oral.stopRecordAndEvaluation {}
        exoPlayer.release()
        super.onDestroyView()
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }

    fun build(type: AnswerType, mode: Int, studentAnswer: StudentAnswer?, subject: String, attachments: ArrayList<Attachment>?) {
        exoPlayer = ExAudioPlayer(requireActivity())

        mType = type
        mMode = mode
        mSubject = subject

        btn_voice.isEnabled = mode == AnswerCardView1.TYPE_EN_LISTEN_AND_READ// 不是所有题都有原声

        attachments?.let {
            if (it.isNotEmpty()) {
                mOriginVoiceUrl = it[0].url
            }
        }

        try {
            oral = (activity as AnswerActivity).getTAIOral()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("音频引擎初始化失败，请退出应用重试，如仍有错误，请联系管理员")
            return
        }
        mCache = ACache.get(requireActivity())

        // 学生答案里面是否保留有录音
        val answer = studentAnswer?.Answer
        if (!TextUtils.isEmpty(answer)) {
            try {
                val data: TAIOralEvaluationRet = Gson().fromJson(answer, TAIOralEvaluationRet::class.java)
                updateScoreView(data)
                if (!TextUtils.isEmpty(data.audioUrl)) {
                    currentAudioUrl = data.audioUrl
                    btn_replay.isEnabled = true
                    btn_record.text = "重录"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {// 学生还未作答
            tv_completion.text = String.format(completionFormat, 0.00)
            tv_fluency.text = String.format(fluencyFormat, 0.00)
            tv_accuracy.text = String.format(accuracyFormat, 0.00)
        }


        btn_record.setOnClickListener {
            // 录音按钮
            exoPlayer.release()
            startRecord()
        }

        btn_voice.setOnClickListener {
            val drawable: Drawable?
            if (isPlaying) {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_origin_voice)
                exoPlayer.release()

                // 主动停止恢复按钮可用
                btn_record.isEnabled = true
                if (!TextUtils.isEmpty(currentAudioUrl)) {
                    btn_replay.isEnabled = true
                }
            } else {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_origin_voice_stop)
                exoPlayer.openAudio(mOriginVoiceUrl)

                // 播放音频时应禁用另外按钮
                btn_record.isEnabled = false
                btn_replay.isEnabled = false
            }
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            btn_voice.setCompoundDrawables(null, drawable, null, null)

            isPlaying = !isPlaying
        }

        btn_replay.setOnClickListener {
            val drawable: Drawable?
            if (isPlaying) {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_student_record)
                exoPlayer.release()

                // 主动停止恢复按钮可用
                btn_record.isEnabled = true
                if (!TextUtils.isEmpty(mOriginVoiceUrl)) {
                    btn_voice.isEnabled = true
                }
            } else {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_student_record_stop)
                exoPlayer.openAudio(currentAudioUrl)

                // 播放音频时应禁用另外按钮
                btn_record.isEnabled = false
                btn_voice.isEnabled = false
            }
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            btn_replay.setCompoundDrawables(null, drawable, null, null)

            isPlaying = !isPlaying
        }

        exoPlayer.setOnCompletionListener {
            isPlaying = false

            val drawableOrigin = ContextCompat.getDrawable(requireContext(), R.drawable.tai_origin_voice)
            val drawableRecord = ContextCompat.getDrawable(requireContext(), R.drawable.tai_student_record)
            drawableOrigin?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            drawableRecord?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            btn_replay.setCompoundDrawables(null, drawableRecord, null, null)
            btn_voice.setCompoundDrawables(null, drawableOrigin, null, null)

            // 音频停止根据是否有内容恢复按钮
            if (!TextUtils.isEmpty(currentAudioUrl)) {
                btn_replay.isEnabled = true
            }
            if (!TextUtils.isEmpty(mOriginVoiceUrl)) {
                btn_voice.isEnabled = true
            }
            btn_record.isEnabled = true
        }
    }

    /**
     * 更新得分视图
     */
    private fun updateScoreView(data: TAIOralEvaluationRet) {
        tv_completion.text = String.format(completionFormat, data.pronCompletion * 100)
        tv_fluency.text = String.format(fluencyFormat, data.pronFluency * 100)
        tv_accuracy.text = String.format(accuracyFormat, data.pronAccuracy)
        tv_suggest_score.text = String.format(suggestScore, data.suggestedScore)
        progress_completion.progress = (data.pronCompletion * 100).toInt()
        progress_fluency.progress = (data.pronFluency * 100).toInt()
        progress_accuracy.progress = data.pronAccuracy.toInt()
        progress_suggest_score.progress = data.suggestedScore.toFloat()

        val msg = Message()
        msg.what = PLAY_RAW
        msg.arg1 = data.suggestedScore.toInt()
        handler.sendMessageDelayed(msg, 50)
    }

    /**
     * 开始录音
     */
    private fun startRecord() {
        if (oral.isRecording) {// 录音中先关闭就好了
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tai_btn_record)
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            btn_record.setCompoundDrawables(null, drawable, null, null)
            btn_record.text = "重录"
            btn_record.textColor = ContextCompat.getColor(requireContext(), R.color.tv33)
            oral.stopRecordAndEvaluation {
                // 停止录音的时候所有按钮恢复可用状态
                if (!TextUtils.isEmpty(mOriginVoiceUrl)) {
                    btn_voice.isEnabled = true
                }
            }
        } else {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop_record)
            drawable?.apply {
                setBounds(0, 0, minimumWidth, minimumHeight)
            }
            btn_record.setCompoundDrawables(null, drawable, null, null)
            btn_record.text = "停止"
            btn_record.textColor = ContextCompat.getColor(requireContext(), R.color.color_red_d1021c)

            oral.setListener(oralListener)

            val recordParam = TAIRecorderParam()
            recordParam.fragSize = 1024
            recordParam.vadEnable = true
            recordParam.vadInterval = 3000
            oral.setRecorderParam(recordParam)
            oral.startRecordAndEvaluation(initTAIParam(mSubject, mMode)) {
                // 开始录制，禁用另外2个音频播放按钮
                btn_voice.isEnabled = false
                btn_replay.isEnabled = false
            }

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
            AnswerCardView1.TYPE_EN_LISTEN_AND_READ -> {// 跟读，目前选最长的
                param.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH
                param.refText = content
            }
        }

        return param
    }


    private val oralListener = object : TAIOralEvaluationListener {// 结果监听

        override fun onVolumeChanged(volume: Int) {}

        override fun onEvaluationData(data: TAIOralEvaluationData, result: TAIOralEvaluationRet?, error: TAIError?) {
            val gson = Gson()
            val retString = gson.toJson(result)
            val errString = gson.toJson(error)

            Log.e("xiaofu", String.format("oralEvaluation:seq:%d, end:%d, error:%s, ret:%s", data.seqId, if (data.bEnd) 1 else 0, errString, retString))


            if (error != null && error.code != 0) {// 发生错误
                onUiThread {
                    toast(error.desc + "，请重新录制")
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_start_record)
                    drawable?.apply {
                        setBounds(0, 0, minimumWidth, minimumHeight)
                    }
                    btn_record.setCompoundDrawables(null, drawable, null, null)
                    oral.stopRecordAndEvaluation {
                        if (!TextUtils.isEmpty(mOriginVoiceUrl)) {
                            btn_voice.isEnabled = true
                        }
                        if (!TextUtils.isEmpty(currentAudioUrl)) {
                            btn_record.isEnabled = true
                        }
                    }
                    btn_record.text = "录音"
                    btn_record.textColor = ContextCompat.getColor(requireContext(), R.color.tv33)
                }
            }
            if (data.bEnd && result != null) {
                onUiThread {
                    updateScoreView(result)
                }
                val studentAnswer = StudentAnswer()
                studentAnswer.Id = mType.id
                studentAnswer.TypeId = mType.typeId
                studentAnswer.Answer = retString
                changeListener?.onDataChanged(studentAnswer)
                currentAudioUrl = result.audioUrl
                try {
                    if (!TextUtils.isEmpty(currentAudioUrl)) {
                        onUiThread {
                            if (!TextUtils.isEmpty(mOriginVoiceUrl)) {
                                btn_voice.isEnabled = true
                            }
                            btn_replay.isEnabled = true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        override fun onEndOfSpeech() {
        }
    }
    private val handler: Handler = SafeHandler(this)

    private fun playRaw(it: Int) {
        when {
            it < 60 -> exoPlayer.openRaw(requireContext(), R.raw.scorebackcopper)
            it in 60..79 -> exoPlayer.openRaw(requireContext(), R.raw.youcandobetter)
            it in 80..89 -> exoPlayer.openRaw(requireContext(), R.raw.good)
            it >= 90 -> exoPlayer.openRaw(requireContext(), R.raw.excellent)
        }
    }

    companion object {
        const val PLAY_RAW = 1314

        private class SafeHandler(val frag: EnTaiAnswerFragment2) : Handler() {
            private var mFragment: WeakReference<EnTaiAnswerFragment2> = WeakReference(frag)
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {
                    PLAY_RAW -> {
                        Log.e("xiaofu", "收到消息")
                        frag.playRaw(msg.arg1)
                    }
                }
            }
        }
    }
}
