package com.cqebd.student.vo.entity

import java.util.*
import kotlin.collections.ArrayList

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
data class FilterData(val status: Int, val Name: String) {

    companion object {
        //作业状态,default = null
        val jobStatus = listOf(
                FilterData(10, "默认"),
                FilterData(-1, "新作业"),
                FilterData(0, "答题中"),
                FilterData(1, "已完成"),
                FilterData(2, "已批阅")
        )
        //年级,default = null
        val grade = listOf(
                FilterData(-1, "全部"),
                FilterData(9, "六年级"),
                FilterData(1, "七年级"),
                FilterData(2, "八年级"),
                FilterData(3, "九年级"),
                FilterData(4, "一年级"),
                FilterData(5, "二年级"),
                FilterData(6, "三年级"),
                FilterData(7, "四年级"),
                FilterData(8, "五年级"))

        //订阅状态
        val subscribeStatus = listOf(
                FilterData(2, "已订阅"),
                FilterData(1, "未订阅")
        )

        //时段
        val dateTime = listOf(
                FilterData(-1, "全部"),
                FilterData(1, "一周之内"),
                FilterData(2, "一月之内"),
                FilterData(3, "两月之内")
        )

        //科目
        val subject: List<FilterData> by lazy {
            val subjectList = UserAccount.load()?.SubjectList
            subjectList?.filter {
                it.Status == 0
            }?.map {
                FilterData(it.Id, it.Name)
            } ?: listOf()
        }

        val subjectAll: List<FilterData> by lazy {
            val mList = ArrayList<FilterData>()
            mList.add(FilterData(-1, "全部"))
            mList.addAll(subject)
            mList
        }


        //作业类型
        val jobType: List<FilterData> by lazy {
            val typeList: ArrayList<UserAccount.JobType> = ArrayList()
            typeList.add(UserAccount.JobType(-1, "全部"))
            UserAccount.load()?.JobTypeList?.let {
                typeList.addAll(it)
            }
            typeList.map {
                FilterData(it.Id, it.Name)
            }
        }
        // 作业分享 年级筛选
        val gradeHomework = listOf(
                FilterData(-1, "全部"),
                FilterData(9, "六年级"),
                FilterData(1, "七年级"),
                FilterData(2, "八年级"),
                FilterData(3, "九年级")
        )
        // 作业分享 题型筛选
        val problemType = listOf(
                FilterData(-1, "全部"),
                FilterData(1, "选择题"),
                FilterData(2, "填空题"),
                FilterData(3, "解答题"),
                FilterData(4, "判断题"),
                FilterData(5, "多选题"),
                FilterData(26, "复合题"),
                FilterData(12, "听力单选题"),
                FilterData(13, "听力填空题")
        )
        // 作业分享 时间筛选
        val dateFilter = listOf(
                FilterData(-1, "全部"),
                FilterData(3, "近三天"),
                FilterData(7, "近一周"),
                FilterData(30, "近一个月")
        )
    }
}