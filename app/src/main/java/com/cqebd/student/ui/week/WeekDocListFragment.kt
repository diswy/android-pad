package com.cqebd.student.ui.week


import android.app.Activity
import android.os.Environment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.cqebd.student.R
import com.cqebd.student.adapter.RemoteFileListAdapter
import com.cqebd.student.myview.ProgressButton
import com.cqebd.student.net.NetClient
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.xiaofu.lib_base_xiaofu.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import org.jetbrains.anko.support.v4.toast
import java.text.DecimalFormat

/**
 * 文件列表
 */
class WeekDocListFragment : BaseFragment() {

    private var mPage = 1

    private lateinit var remoteFileAdapter: RemoteFileListAdapter
    private var disposable: Disposable? = null

    override fun getLayoutRes(): Int = R.layout.fragment_week_doc_list

    override fun initialize(activity: Activity) {

        remoteFileAdapter = RemoteFileListAdapter()

        // 列表初始化
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // 分割线初始化
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = remoteFileAdapter

        remoteFileAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.downloadBtn -> {// 下载按钮
                    try {
                        view as ProgressButton
                        if (view.text == "下载") {

                            val item = remoteFileAdapter.getItem(position)!!

//                            val mFileName = item.FileName
//                            val dot = mFileName.lastIndexOf('.')
//                            val realName = if ((dot > -1) && (dot < mFileName.length)) {
//                                mFileName.substring(0, dot).plus(item.ExtensionName)
//                            } else {
//                                mFileName.plus(item.ExtensionName)
//                            }
                            download(view, item.Url, item.FileName)
                        }
                    } catch (e: Exception) {
                        toast("下载错误，请联系管理员：${e.message}")
                    }
                }
            }
        }

        // 下拉刷新
        smart_refresh_layout.setOnRefreshListener {
            mPage = 1
            requestRemoteDoc()
        }

        // 上拉加载更多
        remoteFileAdapter.setOnLoadMoreListener({
            mPage++
            requestRemoteDoc()
        }, recyclerView)

        requestRemoteDoc()
    }

    private fun requestRemoteDoc() {// 请求远程文档列表
        disposable = NetClient.workService().getRemoteDoc(mPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (mPage == 1) {
                        smart_refresh_layout.finishRefresh()
                        remoteFileAdapter.setNewData(it.data.DataList)
                    } else {
                        remoteFileAdapter.addData(it.data.DataList)
                    }

                    if (it.data.DataList.size < 20) {
                        remoteFileAdapter.loadMoreEnd()
                    } else {
                        remoteFileAdapter.loadMoreComplete()
                    }
                }, {

                }, {

                })

//                .enqueue(object : NetCallBack<BaseResponse<RemoteDoc>>() {
//                    override fun onSucceed(response: BaseResponse<RemoteDoc>?) {
//                        response?.let {
//                            if (mPage == 1) {
//                                smart_refresh_layout.finishRefresh()
//                                remoteFileAdapter.setNewData(it.data.DataList)
//                            } else {
//                                remoteFileAdapter.addData(it.data.DataList)
//                            }
//
//                            if (it.data.DataList.size < 20) {
//                                remoteFileAdapter.loadMoreEnd()
//                            } else {
//                                remoteFileAdapter.loadMoreComplete()
//                            }
//
//                        }
//                    }
//
//                    override fun onFailure() {
//                    }
//                })
    }

    private fun download(btn: ProgressButton, url: String, fileName: String) {
        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/doc/")
        FileDownloader.setup(requireContext())
        FileDownloader.getImpl().create(url)
                .setPath(path + fileName)
                .setListener(object : MyFileDownloadListener() {
                    override fun completed(task: BaseDownloadTask?) {
                        btn.setProgress(100)
                        btn.text = "已下载"
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        val progress = soFarBytes.toDouble() / totalBytes * 100
                        val progressText = DecimalFormat("0.00").format(progress)
                        btn.text = "$progressText%"
                        btn.setProgress(progress.toInt())
                    }
                })
                .start()
    }

    abstract class MyFileDownloadListener : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
        }

        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

}
