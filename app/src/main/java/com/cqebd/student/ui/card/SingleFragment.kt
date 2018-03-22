package com.cqebd.student.ui.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.anko.static.appWidth
import com.anko.static.dp

import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AlternativeContent
import com.cqebd.student.vo.entity.AnswerCardDetailInfo
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer
import kotlinx.android.synthetic.main.item_answer_single_pager.*

import java.util.ArrayList


/**
 * Created by 1 on 2016/7/18.
 */
class SingleFragment : BaseFragment() {
    private var changeListener: DataChangeListener? = null
    private var studentAnswer: StudentAnswer? = null


    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.item_answer_single_pager,container,false)
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
        radio_group.removeAllViews()
        val radioMargin = ((appWidth - 20.dp) / alternativeContents.size - 30.dp) / 2
        for (i in alternativeContents.indices) {
            val radioButton = View.inflate(activity, R.layout.view_radio_button, null) as RadioButton
            radioButton.text = alternativeContents[i].Id
            radio_group.addView(radioButton)
            val layoutParams = radioButton.layoutParams as RadioGroup.LayoutParams
            layoutParams.setMargins(radioMargin, radioMargin, radioMargin, radioMargin)
            layoutParams.width = 30.dp
            layoutParams.height = 30.dp
            radioButton.layoutParams = layoutParams
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked && changeListener != null) {
                    if (this.studentAnswer == null) {
                        this.studentAnswer = StudentAnswer()
                    }
                    this.studentAnswer!!.Answer = radioButton.text.toString()
                    changeListener!!.onDataChanged(this.studentAnswer)
                }
            }
        }
        if (studentAnswer != null) {
            for (i in alternativeContents.indices) {
                if (alternativeContents[i].Id == studentAnswer!!.Answer) {
                    val radioButton = radio_group.getChildAt(i) as RadioButton
                    radioButton.isChecked = true
                    break
                }
            }
        }

    }

    fun build(type: AnswerType, alternativeContents: List<AlternativeContent>, studentAnswer: StudentAnswer?) {
        this.studentAnswer = studentAnswer
        radio_group.removeAllViews()
        val radioMargin = ((appWidth - 20.dp) / alternativeContents.size - 30.dp) / 2
        for (i in alternativeContents.indices) {
            val radioButton = View.inflate(activity, R.layout.view_radio_button, null) as RadioButton
            radioButton.text = alternativeContents[i].Id
            val lp = RadioGroup.LayoutParams(30.dp, 30.dp)
            lp.setMargins(radioMargin, 0, radioMargin, 0)
            radio_group.addView(radioButton, lp)
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked && changeListener != null) {
                    if (this.studentAnswer == null) {
                        this.studentAnswer = StudentAnswer()
                        this.studentAnswer!!.Id = type.id
                        this.studentAnswer!!.TypeId = type.typeId
                    }
                    this.studentAnswer!!.Answer = radioButton.text.toString()
                    changeListener!!.onDataChanged(this.studentAnswer)
                }
            }
        }
        if (studentAnswer != null) {
            for (i in alternativeContents.indices) {
                if (alternativeContents[i].Id == studentAnswer.Answer) {
                    val radioButton = radio_group.getChildAt(i) as RadioButton
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }
}
