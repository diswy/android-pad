package com.cqebd.student.widget.flow

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

@Suppress("PrivatePropertyName")
@SuppressLint("ViewConstructor")
class FlowTagView(context: Context?) : FrameLayout(context), Checkable {
    private var mChecked: Boolean = false
    private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)
    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
            onCheckedChangeListener?.invoke(this,mChecked)
        }
    }

    private var onCheckedChangeListener:((FlowTagView,Boolean)->Unit)?=null

    fun setOnCheckedChangeListener(listener:(FlowTagView,Boolean)->Unit){
        onCheckedChangeListener = listener
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }

    fun tagView(): View = getChildAt(0)

}