package com.cqebd.student.ui

import android.graphics.Color
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.vo.entity.Data
import com.cqebd.student.vo.entity.MessageData
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : BaseActivity() {
    lateinit var adapter: BaseQuickAdapter<Data, BaseViewHolder>
    private var page = 1

    override fun setContentView() {
        setContentView(R.layout.activity_message)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        pageLoadView.load()
        adapter = object : BaseQuickAdapter<Data, BaseViewHolder>(R.layout.item_message_layout) {
            override fun convert(helper: BaseViewHolder, item: Data) {
                helper.setText(R.id.mTvTime, formatTimeYMDHM(item.CreateDateTime))
                        .setText(R.id.mTvStatus, if (item.Status == 1) "已阅读" else "未阅读")
                        .setTextColor(R.id.mTvStatus, if (item.Status == 1) Color.parseColor("#009944") else Color.parseColor("#fb0101"))
                        .setText(R.id.mTvContent, item.Content)
                        .setImageResource(R.id.mIvType, if (item.Type == 4) R.drawable.ic_msg_video else R.drawable.ic_msg_homework)
            }
        }
        recyclerView.adapter = adapter
        getMsg()
    }

    override fun bindEvents() {
        toolbar.setNavigationOnClickListener { finish() }

        smart_refresh_layout.setOnRefreshListener {
            page = 1
            getMsg()
        }

        adapter.setOnLoadMoreListener({
            getMsg()
        }, recyclerView)

        adapter.setOnItemClickListener{ inneradapter, view, position ->
            val data = adapter.getItem(position)
            data?.let {
                it.Status = 1
                adapter.notifyItemChanged(position)
                sendReader(it.Type,it.Id)
                startActivity<AgentWebActivity>("url" to "https://service-student.cqebd.cn/homework/msgdetails?id=${it.Id}")
            }
        }
    }

    private fun getMsg() {
        NetClient.workService()
                .getMsgList(page)
                .enqueue(object : NetCallBack<BaseResponse<MessageData>>() {
                    override fun onSucceed(response: BaseResponse<MessageData>?) {
                        smart_refresh_layout.finishRefresh(true)
                        response?.data?.let {
                            if (it.dataList.isEmpty()){
                                pageLoadView.dataEmpty()
                                return@let
                            }

                            if (page == 1) {
                                adapter.setNewData(it.dataList)
                            } else if (page == it.index) {
                                adapter.addData(it.dataList)
                            }

                            if (it.index < it.pages) {
                                page++
                                adapter.loadMoreComplete()
                            } else if (it.index == it.pages) {
                                adapter.loadMoreEnd()
                            }

                            pageLoadView.hide()
                        }
                    }


                    override fun onFailure() {
                        smart_refresh_layout.finishRefresh(false)
                    }

                })
    }

    private fun sendReader(type: Int, id: Int) {
        NetClient.workService()
                .readMsg(type,id)
                .enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    }

                })
    }

}
