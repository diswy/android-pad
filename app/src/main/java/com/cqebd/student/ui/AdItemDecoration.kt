package com.cqebd.student.ui

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.anko.static.dp

/**
 * 描述
 * Created by gorden on 2018/2/27.
 */
class AdItemDecoration : RecyclerView.ItemDecoration() {
    private val dpGap = 8.dp
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        val position = parent?.getChildAdapterPosition(view)
        val count = parent?.adapter?.itemCount?:0
        outRect?.set(dpGap,0, dpGap,0)
        if (position==0){
            outRect?.left = 2* dpGap
        }else if (position==count-1){
            outRect?.right = 2* dpGap
        }
    }
}