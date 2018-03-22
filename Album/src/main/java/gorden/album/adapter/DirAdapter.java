package gorden.album.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gorden.album.entity.PictureDirectory;
import gorden.album.fragment.AlbumPickerFragment;
import gorden.album.item.ItemDir;
import me.xiaopan.sketch.SketchImageView;

/**
 * document
 * Created by Gordn on 2017/4/1.
 */

public class DirAdapter extends RecyclerView.Adapter<DirAdapter.DirHolder> {
    private AlbumPickerFragment mContext;
    private List<PictureDirectory> directoryList;

    private int lastSelected = 0;

    public DirAdapter(AlbumPickerFragment mContext, List<PictureDirectory> directoryList) {
        this.mContext = mContext;
        this.directoryList = directoryList;
    }

    @Override
    public DirHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new ItemDir(mContext.getContext());
        itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DirHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DirHolder holder, int position) {
        holder.viewSelected.setVisibility(lastSelected == position ? View.VISIBLE : View.GONE);
        PictureDirectory directory = directoryList.get(position);
        holder.textDir.setText(directory.dirName);
        holder.textCount.setText(String.valueOf(directory.pictures.size()).concat(" 张"));
        holder.imgDir.displayImage(directory.coverPicture.path);
    }

    @Override
    public int getItemCount() {
        return directoryList.size();
    }

    class DirHolder extends RecyclerView.ViewHolder {
        ImageView viewSelected;
        SketchImageView imgDir;
        TextView textDir;
        TextView textCount;

        DirHolder(View itemView) {
            super(itemView);
            imgDir = ((ItemDir) itemView).imgDir;
            viewSelected = ((ItemDir) itemView).viewSelected;
            textDir = ((ItemDir) itemView).textDir;
            textCount = ((ItemDir) itemView).textCount;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.toggleDir();
                    if (lastSelected != getLayoutPosition()) {
                        int tempPosition = lastSelected;
                        lastSelected = getLayoutPosition();
                        notifyItemChanged(tempPosition);
                        notifyItemChanged(lastSelected);
                        mContext.onPictureDirectorySelected(directoryList.get(lastSelected), lastSelected == 0);
                    }
                }
            });

        }
    }
}
