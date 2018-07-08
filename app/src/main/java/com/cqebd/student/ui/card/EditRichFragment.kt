package com.cqebd.student.ui.card

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.tools.AlbumHelper
import com.cqebd.student.vo.DataChangeListener
import com.cqebd.student.vo.entity.AnswerCardDetailInfo
import com.cqebd.student.vo.entity.AnswerType
import com.cqebd.student.vo.entity.StudentAnswer
import com.cqebd.student.widget.AvatarImageView
import gorden.album.AlbumPicker
import gorden.lib.anko.static.logError
import gorden.util.XLog
import gorden.widget.dialog.BottomMenuDialog
import kotlinx.android.synthetic.main.item_answer_edit_pager.*
import java.io.File
import java.util.*

/**
 * document
 * Created by Gordn on 2017/3/15.
 */

class EditRichFragment : BaseFragment() {
    internal val strFormat = "<img src=\"%s\"/>"

    internal var changeListener: DataChangeListener? = null
    internal var studentAnswer: StudentAnswer? = null
    internal var type: AnswerType? = null
    private var textWatcher: EditTextWatcher? = null
    internal var initFlag = false
    internal var imageMap: MutableMap<String, String> = HashMap()

    private var albumCallBack: AlbumHelper.AlbumCallBack? = null


    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.item_answer_edit_pager, container, false)
    }


    fun build(answer: AnswerCardDetailInfo.DataBean.QuestionGroup.Answer, localeImageMap: MutableMap<String, String>) {
        val sa = StudentAnswer()
        sa.Answer = if (answer.userAnswer != null) answer.userAnswer else ""
        this.studentAnswer = sa
        ll_pics.visibility = View.VISIBLE
        imageMap = localeImageMap

        if (textWatcher != null) {
            edit_content.removeTextChangedListener(textWatcher)
        }
        textWatcher = EditTextWatcher()
        edit_content.addTextChangedListener(textWatcher)
        img_pic.setTAGChangeListener(object : AvatarImageView.TAGChangeListener {
            override fun tagChanged(tag: Any) {

            }

            override fun tagChanged(key: Int, tag: Any) {
                if (edit_content == null) {
                    return
                }
                if (key == R.id.image_url && !initFlag) {
                    if (studentAnswer == null) {
                        studentAnswer = StudentAnswer()
                    }
                    if (!TextUtils.isEmpty(tag as String)) {
                        val imgAnswer = String.format(strFormat, tag)
                        studentAnswer!!.Answer = edit_content.text.toString() + imgAnswer
                    }
                    if (changeListener != null) {
                        changeListener!!.onDataChanged(studentAnswer)
                    }
                }
                if (key == R.id.image_file_path && !TextUtils.isEmpty(tag as String)) {
                    localeImageMap["1"] = tag
                    Glide.with(App.mContext).asBitmap().load(tag).into(img_pic)
                    btn_delete!!.visibility = View.VISIBLE
                } else if (key == R.id.image_file_path) {
                    btn_delete!!.visibility = View.GONE
                }
            }
        })
        initFlag = true
        edit_content.setText("")
    }

    fun build(type: AnswerType, studentanswer: StudentAnswer, localeImageMap: MutableMap<String, String>) {
        XLog.e("answer  " + studentanswer.Answer)
        logError()
        studentAnswer = studentanswer
        ll_pics.visibility = View.VISIBLE
        this.type = type
        imageMap = localeImageMap
        if (textWatcher != null) {
            edit_content.removeTextChangedListener(textWatcher)
        }
        textWatcher = EditTextWatcher()
        edit_content.addTextChangedListener(textWatcher)
        img_pic.setTAGChangeListener(object : AvatarImageView.TAGChangeListener {
            override fun tagChanged(tag: Any) {

            }

            override fun tagChanged(key: Int, tag: Any) {
                if (edit_content == null) {
                    return
                }
                if (key == R.id.image_url && !initFlag) {
                    if (studentAnswer == null) {
                        studentAnswer = StudentAnswer()
                        studentAnswer!!.Id = type.id
                        studentAnswer!!.TypeId = type.typeId
                    }
                    if (!TextUtils.isEmpty(tag as String)) {
                        val imgAnswer = String.format(strFormat, tag)
                        studentAnswer!!.Answer = edit_content.text.toString() + imgAnswer
                    } else {
                        studentAnswer!!.Answer = edit_content.text.toString()
                        img_pic.setImageBitmap(null)
                    }
                    if (changeListener != null) {
                        changeListener!!.onDataChanged(studentAnswer)
                    }
                }
                if (key == R.id.image_file_path && !TextUtils.isEmpty(tag as String)) {
                    localeImageMap[type.id] = tag
                    Glide.with(App.mContext).asBitmap().load(tag).into(img_pic)
                    btn_delete!!.visibility = View.VISIBLE
                } else if (key == R.id.image_file_path) {
                    btn_delete!!.visibility = View.GONE
                }
            }
        })
        initFlag = true
        edit_content.setText("")
    }

    fun setDataChangeListener(listener: DataChangeListener) {
        changeListener = listener
    }

    private inner class EditTextWatcher : TextWatcher {
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
                if (studentAnswer!!.Answer.contains("<img")) {
                    val imgAnswer = studentAnswer!!.Answer.substring(studentAnswer!!.Answer.indexOf("<img"))
                    studentAnswer!!.Answer = edit_content.text.toString() + imgAnswer
                } else {
                    studentAnswer!!.Answer = edit_content.text.toString()
                }
                if (changeListener != null) {
                    changeListener!!.onDataChanged(studentAnswer)
                }
            } else {

                if (studentAnswer != null) {

                    if (!TextUtils.isEmpty(imageMap[if (type == null) "1" else type!!.id])) {
                        img_pic.setTag(R.id.image_file_path, imageMap[if (type == null) "1" else type!!.id])
                    } else if (studentAnswer!!.Answer.contains("<img")) {
                        var imgAnswer = studentAnswer!!.Answer.substring(studentAnswer!!.Answer.indexOf("<img src=\""))
                        imgAnswer = imgAnswer.replace("<img src=\"", "").replace("\"/>", "").replace("\" />", "")
                        Glide.with(App.mContext).asBitmap().load(imgAnswer).into(img_pic)
                        btn_delete!!.visibility = View.VISIBLE
                        img_pic.setTag(R.id.image_url, imgAnswer)
                    }

                    if (studentAnswer!!.Answer.contains("<img")) {
                        val editAnswer = studentAnswer!!.Answer.substring(0, studentAnswer!!.Answer.indexOf("<img"))
                        edit_content.setText(editAnswer)
                    } else {
                        if (!TextUtils.isEmpty(studentAnswer!!.Answer)) {
                            edit_content.setText(studentAnswer!!.Answer)
                        }
                    }
                }
                initFlag = false
            }
            edit_content.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    override fun bindEvents() {
        icon_camera.setOnClickListener {
            BottomMenuDialog.BottomMenuBuilder()
                    .addItem("拍照") { AlbumPicker.builder().openCamera(this) }
                    .addItem("相册中选择") { AlbumPicker.builder().gridColumns(3).showGif(false).single().start(this) }
                    .addItem("取消", null)
                    .build().show(fragmentManager)
        }

        img_pic.setOnClickListener {
            var filePath: String? = img_pic.getTag(R.id.image_file_path) as? String
            if (TextUtils.isEmpty(filePath) || !File(filePath).exists())
                filePath = img_pic.getTag(R.id.image_url) as String

            if (TextUtils.isEmpty(filePath)) return@setOnClickListener
            AlbumPicker.builder().preview(filePath!!).start(this)
        }

        btn_delete.setOnClickListener {
            AlertDialog.Builder(activity!!)
                    .setMessage("是否要移除当前添加选择的照片")
                    .setPositiveButton("移除") { dialogInterface, i ->
                        img_pic.setTag(R.id.image_url, "")
                        img_pic.setTag(R.id.image_file_path, "")
                        imageMap.remove(type!!.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        XLog.e("requestCode $requestCode")
        AlbumHelper(this).handleResult(requestCode, resultCode, data, albumCallBack, img_pic)
    }

    fun setAlbumCallBack(albumCallBack: AlbumHelper.AlbumCallBack) {
        this.albumCallBack = albumCallBack
    }

    fun hideSoftKeyBord() {
        if (activity != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_content.windowToken, 0) //强制隐藏键盘
        }
    }
}
