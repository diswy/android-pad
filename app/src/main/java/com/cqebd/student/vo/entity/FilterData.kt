package com.cqebd.student.vo.entity

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
data class FilterData(val status: Int, val Name: String) {

    companion object {
        //作业状态,default = null
        val jobStatus = listOf(FilterData(-1, "新作业"),
                FilterData(0, "答题中"),
                FilterData(1, "已完成"),
                FilterData(2, "已批阅"))
        //年级,default = null
        val grade = listOf(FilterData(1, "七年级"), FilterData(2, "八年级"),
                FilterData(3, "九年级"), FilterData(4, "一年级"),
                FilterData(5, "二年级"), FilterData(6, "三年级"),
                FilterData(7, "四年级"), FilterData(8, "五年级"),
                FilterData(9, "六年级"))

        //订阅状态
        val subscribeStatus = listOf(FilterData(1, "未订阅"),
                FilterData(2, "已订阅"))

        //时段
        val dateTime = listOf(FilterData(1, "一周之内"),
                FilterData(2, "一月之内"),
                FilterData(3, "两月之内"))

        //科目
        val subject: List<FilterData> by lazy {
            val subjectList = UserAccount.load()?.SubjectList
            if (subjectList == null) {
                listOf()
            } else {
                subjectList.filter {
                    it.Status == 0
                }.map {
                            FilterData(it.Id, it.Name)
                        }
            }
        }
        //作业类型
        val jobType: List<FilterData> by lazy {
            val typeList = UserAccount.load()?.JobTypeList
            typeList?.map {
                FilterData(it.Id, it.Name)
            } ?: listOf()
        }
    }
}