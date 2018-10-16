package com.cqebd.student.ui.root


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.adapter.RootHomeAdapter
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.entity.RootHomeEntity
import com.cqebd.student.vo.entity.VideoInfo
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.img.loadImage
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.header_home_layout.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*

/**
 * 新首页
 */
class RootHomeFragment : BaseFragment() {
    private lateinit var adapter: BaseMultiItemQuickAdapter<RootHomeEntity, BaseViewHolder>
    private lateinit var headerView: View
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_root_home, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        smart_refresh_layout.isEnableRefresh = false
        headerView = initHeaderView()
        headerView.apply {

            val adList = ArrayList<String>()
            adList.add("http://images.cqebd.cn/style/Content/ad_1.png")
            adList.add("http://images.cqebd.cn/style/Content/ad_2.png")
            adList.add("http://images.cqebd.cn/style/Content/ad_3.png")
            adList.add("http://images.cqebd.cn/style/Content/ad_4.png")
            adList.add("http://images.cqebd.cn/style/Content/ad_5.png")

            mBanner.setImageLoader(GlideLoader())
            mBanner.setImages(adList)
            mBanner.isAutoPlay(false)
            mBanner.start()

//            mADPager.setImagesUrl(arrayListOf(
//                    "http://images.cqebd.cn/style/Content/ad_1.png",
//                    "http://images.cqebd.cn/style/Content/ad_2.png",
//                    "http://images.cqebd.cn/style/Content/ad_3.png",
//                    "http://images.cqebd.cn/style/Content/ad_4.png",
//                    "http://images.cqebd.cn/style/Content/ad_5.png"))
        }
        adapter = RootHomeAdapter(null)
        recyclerView.adapter = adapter
        adapter.addHeaderView(headerView)

        getRecommendCourse()
    }

    private fun initHeaderView(): View {
        return LayoutInflater.from(context).inflate(R.layout.header_home_layout, recyclerView, false)
    }

    private fun getRecommendCourse() {
        NetClient.videoService().getPeriodListTJ()
                .enqueue(object : NetCallBack<BaseResponse<List<VideoInfo>>>() {
                    override fun onSucceed(response: BaseResponse<List<VideoInfo>>?) {
                        Logger.json(Gson().toJson(response?.data))
                        response?.data?.let {
                            adapter.setNewData(excuseData(it.take(5)))
                        }
                    }

                    override fun onFailure() {
                    }

                })
    }


    private fun excuseData(list: List<VideoInfo>): List<RootHomeEntity> {
        val mData = ArrayList<RootHomeEntity>()
        if (list.isNotEmpty()) {
            mData.add(RootHomeEntity("课程推荐"))
            when (list.size) {
                1 -> {
                    mData.add(RootHomeEntity(list[0], null))
                }
                2 -> {
                    mData.add(RootHomeEntity(list[0], list[1]))
                }
                else -> {
                    mData.add(RootHomeEntity(list[0], list[1]))
                    for (i in 2 until list.size) {
                        mData.add(RootHomeEntity(list[i]))
                    }
                }
            }
        }
        return mData
    }

    private inner class GlideLoader : ImageLoader() {
        override fun displayImage(context: Context, path: Any, imageView: ImageView) {
            try {
                loadImage(path as String, imageView)
            } catch (e: Exception) {

            }
        }
    }
}
