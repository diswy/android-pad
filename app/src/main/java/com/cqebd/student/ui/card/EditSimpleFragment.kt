package com.cqebd.student.ui.card

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager


import com.cqebd.student.R
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AnswerCardDetailInfo
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer

import com.cqebd.student.app.BaseFragment
import kotlinx.android.synthetic.main.item_answer_edit_pager.*

/**
 * document
 * Created by Gordn on 2017/3/15.
 */

class EditSimpleFragment : BaseFragment() {
    private var changeListener: DataChangeListener? = null
    private var studentAnswer: StudentAnswer? = null
    private var textWatcher: EditTextWatcher? = null
    private var initFlag = false

    private var type: AnswerType? = null

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.item_answer_edit_pager,container,false)
    }
    

    fun build(answer: AnswerCardDetailInfo.DataBean.QuestionGroup.Answer) {
        val sa = StudentAnswer()
        sa.Answer = if (answer.userAnswer != null) answer.userAnswer else ""
        this.studentAnswer = sa
        ll_pics.visibility = View.GONE
        if (textWatcher != null) {
            edit_content.removeTextChangedListener(textWatcher)
        }
        textWatcher = EditTextWatcher()
        edit_content.addTextChangedListener(textWatcher)
        initFlag = true
        edit_content.setText("")
    }

    fun build(type: AnswerType, studentanswer: StudentAnswer) {

        studentAnswer = studentanswer
        ll_pics.visibility = View.GONE
        this.type = type
        if (textWatcher != null) {
            edit_content.removeTextChangedListener(textWatcher)
        }
        textWatcher = EditTextWatcher()
        edit_content.addTextChangedListener(textWatcher)
        initFlag = true
        edit_content.setText("")
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }

    internal inner class EditTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            edit_content.removeTextChangedListener(this)
            if (studentAnswer == null) {
                studentAnswer = StudentAnswer()
                if (type != null) {
                    studentAnswer!!.Id = type!!.id
                    studentAnswer!!.TypeId = type!!.typeId
                }
            }
            if (!initFlag) {
                studentAnswer!!.Answer = edit_content.text.toString()
                if (changeListener != null) {
                    changeListener!!.onDataChanged(studentAnswer)
                }
            } else {
                /**
                 * 设置默认值
                 */
                if (!TextUtils.isEmpty(studentAnswer!!.Answer)) {
                    edit_content.setText(studentAnswer!!.Answer)
                }
                initFlag = false
            }
            edit_content.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun hideSoftKeyBord() {
        if (activity != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_content.windowToken, 0) //强制隐藏键盘
        }
    }
}
