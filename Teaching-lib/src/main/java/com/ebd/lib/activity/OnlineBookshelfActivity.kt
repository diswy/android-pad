package com.ebd.lib.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ebd.lib.R
import com.ebd.lib.adapter.OnlineBookshelfAdapter
import com.ebd.lib.bean.Grade
import com.ebd.lib.bean.Publish
import com.ebd.lib.bean.Subject
import com.ebd.lib.viewmodel.OnlineBookModel
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.base.BaseToolbarActivity
import kotlinx.android.synthetic.main.activity_online_bookshelf.*
import kotlinx.android.synthetic.main.pop_filter_selcet.*
import kotlinx.android.synthetic.main.recycler_view_layout.*
import org.jetbrains.anko.dip

class OnlineBookshelfActivity : BaseToolbarActivity() {

    private val onlineBookshelfAdapter by lazy { OnlineBookshelfAdapter() }

    private val viewModel by lazy { ViewModelProviders.of(this).get(OnlineBookModel::class.java) }

    private val popView by lazy { LayoutInflater.from(this).inflate(R.layout.pop_filter_selcet, null) }

    private val myFilterRv by lazy { popView.findViewById<RecyclerView>(R.id.onlineRv) }

    private val popwin by lazy {
        val pop = PopupWindow(this)
        pop.setBackgroundDrawable(null)
        pop.isOutsideTouchable = false
        pop.contentView = popView
        pop.width = ViewGroup.LayoutParams.MATCH_PARENT
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return@lazy pop
    }

    override fun setTitle(): String = "书城"

    override fun getView(): Int = R.layout.activity_online_bookshelf

    override fun initialize() {
        mRv.layoutManager = GridLayoutManager(this, 3)
        onlineBookshelfAdapter.bindToRecyclerView(mRv)

        viewModel.eBookList.observe(this, Observer {
            //            Logger.wtf(Gson().toJson(it))
            onlineBookshelfAdapter.setNewData(it)
        })

        // 预加载列表数据
        viewModel.loadGrade()
        viewModel.loadPublish()
        viewModel.loadSubject()

        viewModel.loadEBook()

        myFilterRv.layoutManager = GridLayoutManager(this, 4)

    }

    override fun bindEvent() {
        btnGrade.setOnClickListener {
            if (popwin.isShowing && cbGrade.isChecked) {
                popwin.dismiss()
                cbGrade.isChecked = false
            } else {
                viewModel.gradeList.value.let {
                    myFilterRv.adapter = object : BaseQuickAdapter<Grade, BaseViewHolder>(R.layout.item_online_filter, it) {
                        override fun convert(helper: BaseViewHolder?, item: Grade?) {
                            helper?.setText(R.id.tvFilter, item?.name)
                        }
                    }

                    popwin.showAsDropDown(selHolder)
                    cbGrade.isChecked = true
                    cbSubject.isChecked = false
                    cbPublish.isChecked = false
                }
            }
        }

        btnSubject.setOnClickListener {
            if (popwin.isShowing && cbSubject.isChecked) {
                popwin.dismiss()
                cbSubject.isChecked = false
            } else {
                viewModel.subjectList.value.let {
                    myFilterRv.adapter = object : BaseQuickAdapter<Subject, BaseViewHolder>(R.layout.item_online_filter, it) {
                        override fun convert(helper: BaseViewHolder?, item: Subject?) {
                            helper?.setText(R.id.tvFilter, item?.name)
                        }
                    }

                    popwin.showAsDropDown(selHolder)
                    cbSubject.isChecked = true
                    cbGrade.isChecked = false
                    cbPublish.isChecked = false
                }
            }
        }

        btnPublish.setOnClickListener {
            if (popwin.isShowing && cbPublish.isChecked) {
                popwin.dismiss()
                cbPublish.isChecked = false
            } else {
                viewModel.publishList.value.let {
                    myFilterRv.adapter = object : BaseQuickAdapter<Publish, BaseViewHolder>(R.layout.item_online_filter, it) {
                        override fun convert(helper: BaseViewHolder?, item: Publish?) {
                            helper?.setText(R.id.tvFilter, item?.name)
                        }
                    }

                    popwin.showAsDropDown(selHolder)
                    cbPublish.isChecked = true
                    cbSubject.isChecked = false
                    cbGrade.isChecked = false
                }
            }
        }
    }

}
