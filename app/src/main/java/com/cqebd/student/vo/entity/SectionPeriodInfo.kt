package com.cqebd.student.vo.entity

import com.chad.library.adapter.base.entity.SectionEntity

class SectionPeriodInfo : SectionEntity<PeriodInfo> {
    constructor(item: PeriodInfo) : super(item)
    constructor(header: String?, isHeader: Boolean = true) : super(isHeader, header)
}