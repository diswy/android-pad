package gorden.library.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View

/**
 * 描述
 * Created by gorden on 2018/1/15.
 */
internal class GridItemDecoration(color: Int) : RecyclerView.ItemDecoration() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lineSize = 5

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        val spanCount = spanCount(parent)
        val childCount = parent?.adapter?.itemCount?:0
        val position = parent?.getChildAdapterPosition(view)?:0

        val currentLine = (position+1)/spanCount+if((position+1)%spanCount==0) 0 else 1
        val countLine = childCount/spanCount+if(childCount%spanCount==0) 0 else 1

        if (currentLine<countLine){
            outRect?.bottom = lineSize
        }

        if (position%spanCount<spanCount-1){
            outRect?.right = lineSize
        }
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        drawHorizontal(c,parent)
        drawVertical(c,parent)
    }


    /**
     * 获取列数
     */
    private fun spanCount(parent: RecyclerView?):Int{
        return when(parent?.layoutManager){
            is GridLayoutManager -> (parent.layoutManager as GridLayoutManager).spanCount
            is StaggeredGridLayoutManager -> (parent.layoutManager as StaggeredGridLayoutManager).spanCount
            else -> 1
        }
    }

    private fun drawHorizontal(c: Canvas?,parent: RecyclerView?){
        val childCount = parent?.adapter?.itemCount?:0
        val spanCount = spanCount(parent)
        for (position in 0 until childCount){
            val child = parent?.getChildAt(position)
            val currentLine = (position+1)/spanCount+if((position+1)%spanCount==0) 0 else 1
            val countLine = childCount/spanCount+if(childCount%spanCount==0) 0 else 1
            if (currentLine<countLine){
                val left = child?.left?:0
                val top = child?.bottom?:0
                val right = (child?.right?:0)+lineSize
                val bottom = (child?.bottom?:0)+lineSize
                c?.drawRect(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat(),paint)
            }
        }
    }

    private fun drawVertical(c: Canvas?, parent: RecyclerView?) {
        val spanCount = spanCount(parent)
        val childCount = parent?.adapter?.itemCount?:0
        for (position in 0 until childCount){
            val child = parent?.getChildAt(position)
            if (position%spanCount<spanCount-1){
                val left = child?.right?:0
                val top = child?.top?:0
                val right = left+lineSize
                val bottom = child?.bottom?:0
                c?.drawRect(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat(),paint)
            }
        }
    }
}