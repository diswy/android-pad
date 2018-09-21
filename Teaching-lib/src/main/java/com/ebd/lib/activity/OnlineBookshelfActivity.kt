package com.ebd.lib.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import com.ebd.lib.R
import com.ebd.lib.adapter.OnlineBookshelfAdapter
import com.ebd.lib.viewmodel.OnlineBookModel
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.api.ApiManager
import com.xiaofu.lib_base_xiaofu.base.BaseToolbarActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_online_bookshelf.*
import kotlinx.android.synthetic.main.recycler_view_layout.*

class OnlineBookshelfActivity : BaseToolbarActivity() {

    private val onlineBookshelfAdapter by lazy { OnlineBookshelfAdapter() }

    private val viewModel by lazy { ViewModelProviders.of(this).get(OnlineBookModel::class.java) }

    override fun setTitle(): String = "书城"

    override fun getView(): Int = R.layout.activity_online_bookshelf

    override fun initialize() {
        mRv.layoutManager = GridLayoutManager(this, 3)
        onlineBookshelfAdapter.bindToRecyclerView(mRv)

        viewModel.gradeList.observe(this, Observer {
            mTv.text = Gson().toJson(it)
        })

        viewModel.eBookList.observe(this, Observer {
            Logger.wtf(Gson().toJson(it))
            onlineBookshelfAdapter.setNewData(it)
        })

        mBtn.setOnClickListener {
            viewModel.loadPublish()

        }
        mBtn2.setOnClickListener {
            viewModel.loadSubject()
        }

        viewModel.loadEBook()

    }

    override fun bindEvent() {

    }

}
