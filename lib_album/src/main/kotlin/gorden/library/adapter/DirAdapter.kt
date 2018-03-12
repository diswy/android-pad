package gorden.library.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import gorden.library.aac.PictureViewModel
import gorden.library.entity.dp
import gorden.library.item.ItemDir


/**
 * 相册目录
 */

class DirAdapter(private val viewModel: PictureViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemClickListener: (() -> Unit)? = null

    fun setItemClickListener(listener: () -> Unit) {
        this.itemClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewModel.directoryList.value?.apply {
            val directory = this[position]
            (holder.itemView as? ItemDir)?.apply {
                textDir.text = directory.dirName
                textCount.text = directory.pictures.size.toString().plus(" 张")
                Glide.with(context.applicationContext).asBitmap()
                        .apply(RequestOptions.overrideOf(75.dp))
                        .load(directory.coverPicture.path)
                        .into(imgDir)
                viewSelected.visibility = if (directory == viewModel.currentDirectory.value) View.VISIBLE else View.GONE
                setOnClickListener {
                    if (directory != viewModel.currentDirectory.value) {
                        notifyDataSetChanged()
                        viewModel.currentDirectory.value = directory
                    }
                    itemClickListener?.invoke()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(ItemDir(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }) {}
    }

    override fun getItemCount(): Int {
        return viewModel.directoryList.value?.size ?: 0
    }
}
