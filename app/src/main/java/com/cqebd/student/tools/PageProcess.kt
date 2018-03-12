package com.cqebd.student.tools

/**
 * 分页处理封装
 * Created by gorden on 2017/11/21.
 */
class PageProcess<T> private constructor(private val selector: (T) -> Any) {
    //根据数据，获取当前应该加载的页数
    val pageIndex: Int
        get() = if (data.size < pageSize) {
            1
        } else {
            data.size / pageSize + 1
        }
    private val pageSize = 20
    val data: MutableList<T>

    init {
        data = ArrayList()
    }

    /**
     * 刷新数据，去掉重复
     */
    fun refreshData(elements: List<T>) {
        data.clear()
        data.addAll(elements)
    }

    fun loadMoreData(elements: List<T>) {
        val temp = ArrayList<T>()

        temp.addAll(data)
        temp.addAll(elements)

        data.clear()
        data.addAll(temp.distinctBy {
            selector(it)
        })
        temp.clear()
    }

    fun hasMore(count: Int): Boolean = data.size < count

    companion object {
        fun <T> build(selector: (T) -> Any): PageProcess<T> {
            return PageProcess(selector)
        }
    }
}