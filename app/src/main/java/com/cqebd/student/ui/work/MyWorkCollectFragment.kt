package com.cqebd.student.ui.work


import android.widget.FrameLayout
import com.cqebd.student.R
import com.cqebd.student.`interface`.CustomCallback
import com.cqebd.student.adapter.SubtitleNavigatorAdapter
import com.cqebd.student.constant.Constant
import com.cqebd.student.tools.loginId
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.vo.entity.FilterData
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.fragment_my_collect.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator


/**
 *  收藏
 */
class MyWorkCollectFragment : BaseLazyFragment() {
    private val collectUrlNoSubject = "studentCollect/StudentCollectList?studentid=%s"
    val collectUrlSubject = "studentCollect/StudentCollectList?studentid=%s&SubjectTypeId=%d"

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_collect
    }

    override fun initView() {
        super.initView()

        context?.let {
            // 副标题
            val subCommonNavigator = CommonNavigator(it)
            val mSubtitleNavigatorAdapter = SubtitleNavigatorAdapter(it, FilterData.subjectAll, magic_indicator_subtitle)
            subCommonNavigator.adapter = mSubtitleNavigatorAdapter
            magic_indicator_subtitle.navigator = subCommonNavigator

            mSubtitleNavigatorAdapter.setOnTitleViewOnClickListener(object : CustomCallback.OnPositionListener {
                override fun onClickPos(pos: Int) {
                    when (pos) {
                        0 -> loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlNoSubject, loginId))
                        else -> loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlSubject, loginId, FilterData.subjectAll[pos].status))
                    }
                }
            })
        }
    }

    override fun lazyLoad() {


        loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlNoSubject, loginId))
    }

    override fun onInvisible() {
        // 侧滑菜单启用

    }

    private fun loadWeb(url: String) {
        web_container.removeAllViews()
        AgentWeb.with(this)
                .setAgentWebParent(web_container, FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url)
    }


}
