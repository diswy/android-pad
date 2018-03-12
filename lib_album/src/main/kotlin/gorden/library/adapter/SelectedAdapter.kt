package gorden.library.adapter

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import gorden.library.R
import gorden.library.aac.PictureViewModel
import gorden.library.entity.PREVIEW_ALBUM
import gorden.library.entity.PREVIEW_SELECT
import gorden.library.entity.dp
import gorden.library.item.ItemSelected
import gorden.library.ui.AlbumPreviewFragment
import kotlinx.android.synthetic.main.fragment_album_preview.*

/**
 * 预览界面当前选择的图片
 */
class SelectedAdapter(val paths: List<String>, val viewModel: PictureViewModel, val fragment: AlbumPreviewFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentPath = ""

    init {
        fragment.currentPosition.observe(fragment, Observer {
            val oldPosition: Int = paths.indexOf(currentPath)
            if (viewModel.previewMode == PREVIEW_ALBUM && it != null) {
                currentPath = viewModel.currentDirectory.value!!.pictures[it].path
            } else if (viewModel.previewMode == PREVIEW_SELECT && it != null) {
                currentPath = paths[it]
            }
            val newPosition: Int = paths.indexOf(currentPath)
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
            if (newPosition != -1)
                fragment.recycler_selected.scrollToPosition(newPosition)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = ItemSelected(parent.context)
        return object : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    if (viewModel.previewMode == PREVIEW_ALBUM) {
                        val position = viewModel.currentDirectory.value!!.pictures.indexOfFirst { it.path == paths[layoutPosition] }
                        fragment.currentPosition.value = Math.max(0, position)
                    } else if (viewModel.previewMode == PREVIEW_SELECT) {
                        fragment.currentPosition.value = layoutPosition
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return paths.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as? ItemSelected)?.apply {
            if (currentPath == paths[position]) {
                imageView.setBackgroundResource(R.drawable.album_border)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.background = null
                } else {
                    @Suppress("DEPRECATION")
                    imageView.setBackgroundDrawable(null)
                }
            }
            Glide.with(context).asBitmap().load(paths[position])
                    .apply(RequestOptions.overrideOf(55.dp)).into(imageView)

            if (viewModel.previewMode == PREVIEW_SELECT) {
                if (viewModel.selectedPaths.contains(paths[position])) {
                    imageView.clearColorFilter()
                } else {
                    imageView.setColorFilter(Color.parseColor("#BBEAEAEA"))
                }
            }
        }
    }
}