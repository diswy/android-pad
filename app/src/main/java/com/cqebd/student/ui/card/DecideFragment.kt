package com.cqebd.student.ui.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton


import com.cqebd.student.R
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AnswerCardDetailInfo
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer

import butterknife.BindView
import com.cqebd.student.app.BaseFragment
import kotlinx.android.synthetic.main.item_answer_decide_pager.*


class DecideFragment : BaseFragment() {
    private var changeListener: DataChangeListener? = null

    private var studentAnswer: StudentAnswer? = null

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.item_answer_decide_pager,container,false)
    }
    
    fun build(answer: AnswerCardDetailInfo.DataBean.QuestionGroup.Answer) {
        val sa = StudentAnswer()
        sa.Answer = if (answer.userAnswer != null) answer.userAnswer else ""
        this.studentAnswer = sa

        radio_def.isChecked = true

        radio_right.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && changeListener != null) {
                if (studentAnswer == null) {
                    studentAnswer = StudentAnswer()
                }
                studentAnswer!!.Answer = "T"
                changeListener!!.onDataChanged(studentAnswer)
            }
        }
        radio_err.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && changeListener != null) {
                if (studentAnswer == null) {
                    studentAnswer = StudentAnswer()
                }
                studentAnswer!!.Answer = "F"
                changeListener!!.onDataChanged(studentAnswer)
            }
        }

        if (studentAnswer != null) {
            if (studentAnswer!!.Answer == "T") {
                radio_right.isChecked = true
            } else if (studentAnswer!!.Answer == "F") {
                radio_err.isChecked = true
            }
        }
    }

    fun build(type: AnswerType, studentanswer: StudentAnswer) {
        studentAnswer = studentanswer

        radio_def.isChecked = true

        radio_right.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && changeListener != null) {
                if (studentAnswer == null) {
                    studentAnswer = StudentAnswer()
                    studentAnswer!!.Id = type.id
                    studentAnswer!!.TypeId = type.typeId
                }
                studentAnswer!!.Answer = "T"
                changeListener!!.onDataChanged(studentAnswer)
            }
        }
        radio_err.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && changeListener != null) {
                if (studentAnswer == null) {
                    studentAnswer = StudentAnswer()
                    studentAnswer!!.Id = type.id
                    studentAnswer!!.TypeId = type.typeId
                }
                studentAnswer!!.Answer = "F"
                changeListener!!.onDataChanged(studentAnswer)
            }
        }

        if (studentAnswer != null) {
            if (studentAnswer!!.Answer == "T") {
                radio_right.isChecked = true
            } else if (studentAnswer!!.Answer == "F") {
                radio_err.isChecked = true
            }
        }
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }
}
