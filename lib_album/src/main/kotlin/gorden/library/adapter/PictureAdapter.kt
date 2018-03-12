package gorden.library.adapter

import android.content.res.Configuration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import gorden.library.Album
import gorden.library.aac.PictureViewModel
import gorden.library.entity.SINGLE_SELECT_MODE
import gorden.library.item.ItemCamera
import gorden.library.item.ItemPicture
import gorden.library.ui.AlbumPickerFragment

/**
 * 照片adapter
 * Created by Gorden on 2017/4/2.
 */

class PictureAdapter(val fragment: AlbumPickerFragment, val viewModel: PictureViewModel) : RecyclerView.Adapter<PictureAdapter.PictureHolder>() {
    private var itemClickListener: ((position: Int) -> Unit)? = null
    fun setItemClickListener(listener: (position: Int) -> Unit) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        return if (viewType % 2 == 1) {
            val itemView = ItemCamera(parent.context)
            itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewModel.orientationItemSize)
            PictureHolder(itemView, true)
        } else {
            val itemView = ItemPicture(parent.context, viewModel.orientationItemSize)
            itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            PictureHolder(itemView, false)
        }
    }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        viewModel.currentDirectory.value?.apply {
            var mPosition = position
            if (getItemViewType(position) % 2 == 1) return
            mPosition -= if (showCamera) 1 else 0
            (holder.itemView as ItemPicture).apply {
                Glide.with(context.applicationContext).asBitmap().apply(RequestOptions.overrideOf(viewModel.orientationItemSize))
                        .load(pictures[mPosition].path).into(imgPicture)
                imgCheck.isChecked = viewModel.selectedPaths.contains(pictures[mPosition].path)
                viewShadow.visibility = if (imgCheck.isChecked) View.VISIBLE else View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewModel.currentDirectory.value?.showCamera == true && position == 0) {
            if (viewModel.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 3
        } else {
            if (viewModel.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
        }
    }

    override fun getItemCount(): Int {
        viewModel.currentDirectory.value?.apply {
            return if (showCamera) pictures.size + 1 else pictures.size
        }
        return if (viewModel.showCamera) 1 else 0
    }

    inner class PictureHolder(itemView: View, camera: Boolean) : RecyclerView.ViewHolder(itemView) {
        init {
            if (camera) {
                itemView.setOnClickListener { Album.create().openCamera(fragment) }
            } else {
                (itemView as ItemPicture).apply {
                    if (viewModel.selectMode == SINGLE_SELECT_MODE) {
                        imgCheck.visibility = View.GONE
                    }

                    imgCheck.setOnCheckedChangeListener { compoundButton, isChecked ->
                        if (!compoundButton.isPressed) return@setOnCheckedChangeListener
                        if (isChecked&&viewModel.selectedPaths.size >= viewModel.maxCount){
                            Toast.makeText(fragment.context, "您最多只能选择" + viewModel.maxCount + "张照片", Toast.LENGTH_SHORT).show()
                            compoundButton.isChecked = false
                            return@setOnCheckedChangeListener
                        }

                        val position = layoutPosition - if (viewModel.currentDirectory.value?.showCamera == true) 1 else 0
                        viewShadow.visibility = if (isChecked) View.VISIBLE else View.GONE
                        viewModel.currentDirectory.value?.apply {
                            if (isChecked && !viewModel.selectedPaths.contains(pictures[position].path)) {
                                viewModel.selectedPaths.add(pictures[position].path)
                            } else if (!isChecked && viewModel.selectedPaths.contains(pictures[position].path)) {
                                viewModel.selectedPaths.remove(pictures[position].path)
                            }
                            fragment.refreshConfirm()
                        }
                    }

                    imgPicture.setOnClickListener({
                        val position = layoutPosition - if (viewModel.currentDirectory.value?.showCamera == true) 1 else 0
                        itemClickListener?.invoke(position)
                    })
                }
            }
        }
    }
}
