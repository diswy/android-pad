package com.cqebd.student.ui.card

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.cqebd.student.R
import com.cqebd.student.tools.StringUtils
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AlternativeContent
import com.cqebd.student.vo.entity.AnswerCardDetailInfo
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import com.anko.static.appWidth
import com.anko.static.dp
import com.cqebd.student.app.BaseFragment
import kotlinx.android.synthetic.main.item_answer_multi_pager.*


class MultiFragment : BaseFragment() {
    private var changeListener: DataChangeListener? = null
    private var studentAnswer: StudentAnswer? = null

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.item_answer_multi_pager,container,false)
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }

    private fun createAlternativeContent(answer: AnswerCardDetailInfo.DataBean.QuestionGroup.Answer): List<AlternativeContent> {
        val list = ArrayList<AlternativeContent>()
        val chars = "ABCDEFGHIJKLMN"
        for (i in 0 until answer.option) {
            val content = AlternativeContent()
            content.Id = chars.substring(i, i + 1)
            content.Content = ""

            list.add(content)
        }
        return list
    }

    fun build(answer: AnswerCardDetailInfo.DataBean.QuestionGroup.Answer) {
        val alternativeContents = createAlternativeContent(answer)
        val sa = StudentAnswer()
        sa.Answer = answer.userAnswer

        this.studentAnswer = sa
        multi_content.removeAllViews()
        val radioMargin = ((appWidth - 20.dp) / alternativeContents.size - 30.dp) / 2
        for (i in alternativeContents.indices) {
            val radioButton = View.inflate(activity, R.layout.view_multi_button, null) as TextView
            radioButton.text = alternativeContents[i].Id
            radioButton.setOnClickListener { v ->
                v.isSelected = !v.isSelected
                if (this.studentAnswer == null) {
                    this.studentAnswer = StudentAnswer()
                }
                if (v.isSelected) {
                    if (TextUtils.isEmpty(this.studentAnswer!!.Answer)) {
                        this.studentAnswer!!.Answer = radioButton.text.toString()
                    } else {
                        val answers = this.studentAnswer!!.Answer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var newAnswers = arrayOfNulls<String>(answers.size + 1)
                        System.arraycopy(answers, 0, newAnswers, 0, answers.size)
                        newAnswers[newAnswers.size - 1] = radioButton.text.toString()
                        val answerList = ArrayList(Arrays.asList<String>(*newAnswers))
                        answerList.sort()
                        newAnswers = answerList.toTypedArray()
                        this.studentAnswer!!.Answer = StringUtils.join(newAnswers, ",")
                        this.studentAnswer!!.Answer = StringUtils.join(newAnswers, ",")
                    }
                } else {
                    if (!TextUtils.isEmpty(this.studentAnswer!!.Answer)) {
                        val answers = this.studentAnswer!!.Answer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val answerList = ArrayList(Arrays.asList(*answers))
                        for (i1 in answerList.indices) {
                            if (answerList[i1] == radioButton.text.toString()) {
                                answerList.removeAt(i1)
                                break
                            }
                        }
                        val newAnswers = answerList.toTypedArray()
                        this.studentAnswer!!.Answer = StringUtils.join(newAnswers, ",")
                    }
                }
                if (changeListener != null) {
                    changeListener!!.onDataChanged(this.studentAnswer)
                }
            }
            multi_content.addView(radioButton)
            val layoutParams = radioButton.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(radioMargin, 0, radioMargin, 0)
            layoutParams.width = 30.dp
            layoutParams.height = 30.dp
            radioButton.layoutParams = layoutParams
        }
        if (studentAnswer != null) {
            for (i in alternativeContents.indices) {
                if (studentAnswer!!.Answer.contains(alternativeContents[i].Id)) {
                    val radioButton = multi_content.getChildAt(i) as TextView
                    radioButton.isSelected = true
                }
            }
        }
    }

    fun build(type: AnswerType, alternativeContents: List<AlternativeContent>, studentAnswer: StudentAnswer?) {
        this.studentAnswer = studentAnswer
        multi_content.removeAllViews()
        val radioMargin = ((appWidth - 20.dp) / alternativeContents.size - 30.dp) / 2
        for (i in alternativeContents.indices) {
            val radioButton = View.inflate(activity, R.layout.view_multi_button, null) as TextView
            radioButton.text = alternativeContents[i].Id
            radioButton.setOnClickListener { v ->
                v.isSelected = !v.isSelected
                if (this.studentAnswer == null) {
                    this.studentAnswer = StudentAnswer()
                    this.studentAnswer!!.Id = type.id
                    this.studentAnswer!!.TypeId = type.typeId
                }
                if (v.isSelected) {
                    if (TextUtils.isEmpty(this.studentAnswer!!.Answer)) {
                        this.studentAnswer!!.Answer = radioButton.text.toString()
                    } else {
                        val answers = this.studentAnswer!!.Answer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var newAnswers = arrayOfNulls<String>(answers.size + 1)
                        System.arraycopy(answers, 0, newAnswers, 0, answers.size)
                        newAnswers[newAnswers.size - 1] = radioButton.text.toString()
                        val answerList = ArrayList(Arrays.asList<String>(*newAnswers))
                        Collections.sort(answerList)
                        newAnswers = answerList.toTypedArray()
                        this.studentAnswer!!.Answer = StringUtils.join(newAnswers, ",")
                    }
                } else {
                    if (!TextUtils.isEmpty(this.studentAnswer!!.Answer)) {
                        val answers = this.studentAnswer!!.Answer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val answerList = ArrayList(Arrays.asList(*answers))
                        for (i1 in answerList.indices) {
                            if (answerList[i1] == radioButton.text.toString()) {
                                answerList.removeAt(i1)
                                break
                            }
                        }
                        val newAnswers = answerList.toTypedArray()
                        this.studentAnswer!!.Answer = StringUtils.join(newAnswers, ",")
                    }
                }
                if (changeListener != null) {
                    changeListener!!.onDataChanged(this.studentAnswer)
                }
            }
            multi_content.addView(radioButton)
            val layoutParams = radioButton.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(radioMargin, radioMargin, radioMargin, radioMargin)
            layoutParams.width = 30.dp
            layoutParams.height = 30.dp
            radioButton.layoutParams = layoutParams
        }
        if (studentAnswer != null) {
            for (i in alternativeContents.indices) {
                if (studentAnswer.Answer.contains(alternativeContents[i].Id)) {
                    val radioButton = multi_content.getChildAt(i) as TextView
                    radioButton.isSelected = true
                }
            }
        }


    }
}
