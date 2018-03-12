package com.cqebd.student.widget.flow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.anko.static.dp
import kotlin.math.min

@Suppress("unused")
/**
 * 描述
 * Created by gorden on 2018/2/28.
 */
class TagFlowLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : KFlowLayout(context, attrs, defStyleAttr){
    private var motionEvent: MotionEvent?=null
    private val selectedPositions = HashSet<Int>()
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        (0 until childCount).forEach {
            val tagView = getChildAt(it)
            if (tagView.isShown&&tagView.tagView().visibility== View.GONE){
                tagView.visibility = View.GONE
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private var selectedChangeListener:((Set<Int>)->Unit)? = null
    private var tagClickListener:((view:View,position:Int)->Unit)?=null
    fun setSelectedChangeListener(listener:(Set<Int>)->Unit){
        selectedChangeListener = listener
    }
    fun setTagClickListener(listener:(view:View,position:Int)->Unit){
        tagClickListener = listener
    }

    fun <T> setAdapter(tagAdapter: FlowTagAdapter<T>){
        selectedPositions.clear()
        removeAllViews()

        (0 until  tagAdapter.getCount()).forEach {
            val tagView = tagAdapter.createView(this,it,tagAdapter.getItem(it))
            val root = FlowTagView(context)
            tagView.isDuplicateParentStateEnabled = true

            if (tagView.layoutParams!=null){
                root.layoutParams = tagView.layoutParams
            }else{
                val lp = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                lp.setMargins(5.dp,5.dp,5.dp,5.dp)
                root.layoutParams = lp
            }
            root.addView(tagView)
            addView(root)
        }

        if (minSelect>0){
            (0 until min(minSelect,tagAdapter.getCount())).forEach {
                selectedPositions.add(it)
                getChildAt(it).isChecked = true
            }
            selectedChangeListener?.invoke(selectedPositions)
        }
    }

    /**
     * 设置选中的item,需在设置adapter之后
     */
    fun setSelectedList(vararg positions: Int) {
        setSelectedList(positions.toSet())
    }

    fun setSelectedList(positions: List<Int>?) {
        setSelectedList(positions?.toSet())
    }

    private fun setSelectedList(set: Set<Int>?) {
        selectedPositions.clear()
        if (set != null && set.isNotEmpty()) {
            selectedPositions.addAll(set)
        }
        notifyData()
        selectedChangeListener?.invoke(selectedPositions)
    }


    private fun notifyData(){
        for (index in 0 until childCount){
            val tagView = getChildAt(index)
            tagView.isChecked = selectedPositions.contains(index)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action==MotionEvent.ACTION_UP){
            motionEvent = MotionEvent.obtain(event)
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        if (motionEvent==null)return super.performClick()
        val child = findChild(motionEvent!!.x.toInt(), motionEvent!!.y.toInt())
        val position = indexOfChild(child)

        if (child!=null){
            doSelect(child, position)
            tagClickListener?.invoke(child.tagView(),position)
        }
        return true
    }

    private fun doSelect(child: FlowTagView, position: Int) {
        if (!child.isChecked){
            if (maxSelect==1&&selectedPositions.size==1){
                val prePosition = selectedPositions.iterator().next()
                val preTagView = getChildAt(prePosition)
                preTagView.isChecked = false
                child.isChecked = true
                selectedPositions.remove(prePosition)
                selectedPositions.add(position)
            }else{
                if (maxSelect>0&&selectedPositions.size>=maxSelect){
                    return
                }
                child.isChecked = true
                selectedPositions.add(position)
            }
            selectedChangeListener?.invoke(selectedPositions)
        }else if (maxSelect*minSelect!=1){
            child.isChecked = false
            selectedPositions.remove(position)
            selectedChangeListener?.invoke(selectedPositions)
        }
    }

    private fun findChild(x: Int, y: Int): FlowTagView? {
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v.visibility == View.GONE) continue
            val outRect = Rect()
            v.getHitRect(outRect)
            if (outRect.contains(x, y)) {
                return v
            }
        }
        return null
    }

    override fun getChildAt(index: Int): FlowTagView {
        return super.getChildAt(index) as FlowTagView
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("key_default", super.onSaveInstanceState())

        var selectPos = ""

        if (selectedPositions.size>0){
            selectedPositions.forEach {
                selectPos.plus(it).plus("|")
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString("key_choose_pos", selectPos)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val mSelectPos = state.getString("key_choose_pos")
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (pos in split) {
                    val index = Integer.parseInt(pos)
                    selectedPositions.add(index)
                    getChildAt(index).isChecked = true
                }
            }
            return super.onRestoreInstanceState(state.getParcelable("key_default"))
        }
        super.onRestoreInstanceState(state)
    }

    fun setSelectedRange(min:Int,max:Int){
        minSelect = min
        maxSelect = max
    }

    fun getSelectedList() = selectedPositions.toList()
}