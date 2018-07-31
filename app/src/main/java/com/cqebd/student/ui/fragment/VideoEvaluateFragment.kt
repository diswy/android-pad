package com.cqebd.student.ui.fragment


import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.StringUtils
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.vo.entity.UserAccount
import com.cqebd.student.vo.entity.VideoEvaluate
import gorden.behavior.LoadingDialog
import kotlinx.android.synthetic.main.fragment_video_evaluate.*
import kotlinx.android.synthetic.main.item_video_evaluate.view.*
import okhttp3.ResponseBody
import retrofit2.Call


/**
 * 点播视频评价
 *
 */
class VideoEvaluateFragment : BaseLazyFragment(), TextWatcher {
    private var call: Call<BaseResponse<List<VideoEvaluate>>>? = null
    private lateinit var adapter: BaseQuickAdapter<VideoEvaluate, BaseViewHolder>

    private var videoId: Int = 0
    override fun afterTextChanged(s: Editable?) {
        mBtnSend.isEnabled = !TextUtils.isEmpty(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_video_evaluate
    }

    override fun lazyLoad() {
        mEtEvaluate.addTextChangedListener(this)
        adapter = object : BaseQuickAdapter<VideoEvaluate, BaseViewHolder>(R.layout.item_video_evaluate) {
            override fun convert(helper: BaseViewHolder?, item: VideoEvaluate) {
                helper?.itemView?.apply {
                    GlideApp.with(App.mContext).load(item.StudentPhoto).circleCrop().placeholder(R.drawable.ic_avatar).into(mAvatar)
                    mUserName.text = item.NickName
                    mContent.text = item.Comment
                    mDate.text = formatTimeYMDHM(item.CreateDateTime)
                }
            }
        }
        mRv.adapter = adapter
        arguments?.let {
            videoId = it.getInt("id", 0)
        }

        mRefreshLayout.setOnRefreshListener {
            getEvaluate()
        }

        mBtnSend.setOnClickListener {
            addEvaluate(mEtEvaluate.text.toString().trim())
        }

        call = NetClient.videoService().getVideoEvaluate(videoId)
        getEvaluate()
    }

    override fun onInvisible() {
        call?.cancel()
    }

    private fun getEvaluate() {
        call?.let {
            if (it.isExecuted)
                call = NetClient.videoService().getVideoEvaluate(videoId)
        }
        call?.enqueue(object : NetCallBack<BaseResponse<List<VideoEvaluate>>>() {

            override fun onSucceed(response: BaseResponse<List<VideoEvaluate>>?) {
                mRefreshLayout.finishRefresh(true)
                response?.data?.let {
                    adapter.setNewData(it)
                }
            }

            override fun onFailure() {
                mRefreshLayout.finishRefresh(false)
            }

        })
    }

    private fun addEvaluate(content: String) {
        LoadingDialog.show(activity, "正在提交...")
        val mUser = UserAccount.load()
        mUser?.let {
            NetClient.videoService()
                    .addEvaluate(videoId, it.Name, StringUtils.getUnicodeString(content))
                    .enqueue(object :NetCallBack<ResponseBody>(){
                        override fun onSucceed(response: ResponseBody?) {
                            LoadingDialog.stop()
                            getEvaluate()
                            mEtEvaluate.setText("")
                        }

                        override fun onFailure() {
                            LoadingDialog.stop()
                        }
                    })
        }
    }
}
