package gorden.library.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import gorden.library.Album.Companion.KEY_DEL_IMAGES
import gorden.library.Album.Companion.KEY_IMAGES
import gorden.library.R
import gorden.library.aac.PictureViewModel
import gorden.library.adapter.SelectedAdapter
import gorden.library.core.ZoomOutPageTransformer
import gorden.library.entity.*
import kotlinx.android.synthetic.main.fragment_album_preview.*


/**
 * 图片预览
 */
class AlbumPreviewFragment : Fragment() {
    private lateinit var pictureViewModel: PictureViewModel
    private lateinit var adapterSelected: SelectedAdapter
    val currentPosition = MutableLiveData<Int>()
    private var scroller: Scroller? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_preview, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureViewModel = ViewModelProviders.of(activity!!).get(PictureViewModel::class.java)

        currentPosition.value = pictureViewModel.previewPosition

        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            scroller = Scroller(context)
            field.set(pager_image, scroller)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }


        var previewPaths: List<String> = pictureViewModel.selectedPaths
        when (pictureViewModel.previewMode) {
            PREVIEW_ALBUM -> {
                btn_confirm.visibility = VISIBLE
                previewPaths = pictureViewModel.currentDirectory.value?.pictures!!.map { it.path }
                adapterSelected = SelectedAdapter(pictureViewModel.selectedPaths, pictureViewModel, this)
                recycler_selected.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                (recycler_selected.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                recycler_selected.adapter = adapterSelected
            }
            PREVIEW_SELECT -> {
                btn_confirm.visibility = VISIBLE
                previewPaths = pictureViewModel.selectedPaths.toList()
                adapterSelected = SelectedAdapter(pictureViewModel.selectedPaths.toList(), pictureViewModel, this)
                recycler_selected.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                (recycler_selected.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                recycler_selected.adapter = adapterSelected
            }
            PREVIEW_PREVIEW -> {
                lin_bottom.visibility = GONE
            }
            PREVIEW_DELETE -> {
                btn_delete.visibility = VISIBLE
                lin_bottom.visibility = GONE
            }
        }

        currentPosition.observe(this, Observer {
            it?.apply {
                when (pictureViewModel.previewMode) {
                    PREVIEW_ALBUM -> {
                        checkbox.isChecked = pictureViewModel.selectedPaths.contains(pictureViewModel.currentDirectory.value!!.pictures[this].path)
                    }
                    PREVIEW_SELECT -> {
                        checkbox.isChecked = pictureViewModel.selectedPaths.contains(adapterSelected.paths[this])
                    }
                }
                text_position.text = "${this + 1}/${previewPaths.size}"
                scroller?.interception = true
                pager_image.setCurrentItem(this, true)
                scroller?.interception = false
            }
        })

        refreshConfirm()

        pager_image.setPageTransformer(false, ZoomOutPageTransformer())

        pager_image.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return AlbumImageFragment().apply {
                    arguments = Bundle().apply { putString("url", previewPaths[position]) }
                }
            }

            override fun getCount(): Int {
                return previewPaths.size
            }

            override fun getItemPosition(`object`: Any): Int {
                return PagerAdapter.POSITION_NONE
            }
        }

        pager_image.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentPosition.value = position
            }
        })

        checkbox.setOnCheckedChangeListener { compoundButton, checked ->
            if (compoundButton.isPressed) {//手动选择
                if (checked) {
                    if (pictureViewModel.selectedPaths.size < pictureViewModel.maxCount) {
                        if (pictureViewModel.previewMode == PREVIEW_SELECT) {
                            pictureViewModel.selectedPaths.add(previewPaths[currentPosition.value!!])
                            adapterSelected.notifyItemChanged(currentPosition.value!!)
                        } else {
                            pictureViewModel.selectedPaths.add(previewPaths[currentPosition.value!!])
                            if (pictureViewModel.selectedPaths.size == 1) {//刷新闪烁的问题
                                adapterSelected.notifyDataSetChanged()
                            } else {
                                adapterSelected.notifyItemInserted(pictureViewModel.selectedPaths.size - 1)
                            }
                            recycler_selected.scrollToPosition(pictureViewModel.selectedPaths.size - 1)
                        }
                        pictureViewModel.notifySelected()
                        refreshConfirm()
                    } else {
                        compoundButton.isChecked = false
                        Toast.makeText(activity, "您最多只能选择" + pictureViewModel.maxCount + "张照片", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (pictureViewModel.previewMode == PREVIEW_SELECT) {
                        pictureViewModel.selectedPaths.remove(previewPaths[currentPosition.value!!])
                        adapterSelected.notifyItemChanged(currentPosition.value!!)
                    } else {
                        val index = pictureViewModel.selectedPaths.indexOf(previewPaths[currentPosition.value!!])
                        pictureViewModel.selectedPaths.removeAt(index)
                        adapterSelected.notifyItemRemoved(index)
                    }
                    pictureViewModel.notifySelected()
                    refreshConfirm()
                }
            }
        }


        btn_back.setOnClickListener {
            activity?.onBackPressed()
        }

        btn_confirm.setOnClickListener {
            val resultData = Intent()
            if (pictureViewModel.selectedPaths.isEmpty()) {
                resultData.putStringArrayListExtra(KEY_IMAGES, arrayListOf(previewPaths[currentPosition.value!!]))
            } else {
                resultData.putStringArrayListExtra(KEY_IMAGES, pictureViewModel.selectedPaths)
            }
            activity?.setResult(RESULT_OK, resultData)
            activity?.finish()
        }

        btn_delete.setOnClickListener {
            AlertDialog.Builder(activity!!).setMessage("要删除这张照片吗?")
                    .setPositiveButton("确定", { _, _ ->
                        pictureViewModel.delList.add((previewPaths as ArrayList).removeAt(currentPosition.value!!))
                        if (previewPaths.isEmpty()) {
                            val resultData = Intent()
                            resultData.putStringArrayListExtra(KEY_DEL_IMAGES, pictureViewModel.delList)
                            activity?.setResult(RESULT_OK, resultData)
                            activity?.onBackPressed()
                        } else {
                            pager_image.adapter?.notifyDataSetChanged()
                            text_position.text = "${currentPosition.value!! + 1}/${previewPaths.size}"
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show().apply {
                        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY)
                    }
        }
    }

    private fun refreshConfirm() {
        when (pictureViewModel.selectMode) {
            SINGLE_SELECT_MODE -> {
//                checkbox.visibility = GONE
//                view_line.visibility = GONE
//                recycler_selected.visibility = GONE
                lin_bottom.visibility = GONE
            }
            MULTI_SELECT_MODE -> {
                val size = pictureViewModel.selectedPaths.size
                btn_confirm.text = if (size > 0 && pictureViewModel.maxCount > 1)
                    String.format(getString(R.string.album_str_complete), "($size/${pictureViewModel.maxCount})")
                else
                    String.format(getString(R.string.album_str_complete), "")
                if (size > 0) {
                    view_line.visibility = VISIBLE
                    recycler_selected.visibility = VISIBLE
                } else {
                    view_line.visibility = GONE
                    recycler_selected.visibility = GONE
                }
            }
        }
    }

    private class Scroller(context: Context?) : android.widget.Scroller(context) {
        var interception: Boolean = false
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, if (interception) 0 else duration)
        }
    }
}