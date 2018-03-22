package gorden.album.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.List;

import gorden.album.AlbumPicker;
import gorden.album.entity.Picture;
import gorden.album.fragment.AlbumPickerFragment;
import gorden.album.item.ItemCamera;
import gorden.album.item.ItemPicture;
import me.xiaopan.sketch.SketchImageView;

import static gorden.album.AlbumPicker.EXTRA_PREVIEW_ENABLED;
import static gorden.album.fragment.AlbumPickerFragment.MULTI_SELECT_MODE;
import static gorden.album.fragment.AlbumPickerFragment.SINGLE_SELECT_MODE;

/**
 * 照片adapter
 * Created by Gorden on 2017/4/2.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {
    private AlbumPickerFragment mContext;
    private List<Picture> pictureList;
    private boolean showCamera;
    private boolean previewEnabled;
    private int TYPE_CAMERA = 1;

    private int itemSize;

    public PictureAdapter(AlbumPickerFragment mContext, List<Picture> pictureList, boolean showCamera) {
        this.mContext = mContext;
        this.pictureList = pictureList;
        this.showCamera = showCamera;
        itemSize = mContext.appWidth() / mContext.pickerGridColumn;
        previewEnabled = mContext.getArguments().getBoolean(EXTRA_PREVIEW_ENABLED, true);
    }

    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View itemView = new ItemCamera(mContext.getContext());
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemSize));
            return new PictureHolder(itemView, true);
        } else {
            View itemView = new ItemPicture(parent.getContext(), itemSize);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new PictureHolder(itemView, false);
        }
    }

    @Override
    public void onBindViewHolder(PictureHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CAMERA) return;
        position -= showCamera ? 1 : 0;
        Picture picture = pictureList.get(position);
        holder.imgPicture.displayImage(picture.path);
        holder.imgCheck.setChecked(mContext.selectPath.contains(pictureList.get(position).path));
        holder.viewShadow.setVisibility(holder.imgCheck.isChecked() ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        int count = (pictureList!=null?pictureList.size():0);
        return showCamera ? count + 1 : count;
    }

    class PictureHolder extends RecyclerView.ViewHolder {
        SketchImageView imgPicture;
        CheckBox imgCheck;
        View viewShadow;
        View viewClicked;

        PictureHolder(View itemView, boolean camera) {
            super(itemView);
            if (camera) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlbumPicker.builder().openCamera(mContext);
                    }
                });
            } else {
                imgPicture = ((ItemPicture) itemView).imgPicture;
                imgCheck = ((ItemPicture) itemView).imgCheck;
                viewShadow = ((ItemPicture) itemView).viewShadow;
                viewClicked = ((ItemPicture) itemView).viewClicked;

                if (mContext.pickerModel == SINGLE_SELECT_MODE){
                    viewClicked.setVisibility(View.GONE);
                    imgCheck.setVisibility(View.GONE);
                }

                viewClicked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleCheck();
                    }
                });

                imgPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((previewEnabled && mContext.pickerModel == MULTI_SELECT_MODE) || mContext.pickerModel == SINGLE_SELECT_MODE) {
                            int position = getLayoutPosition() - (showCamera ? 1 : 0);
                            mContext.preViewImage(pictureList, position);
                        } else {
                            toggleCheck();
                        }
                    }
                });
            }
        }

        void toggleCheck() {
            boolean isChecked = !imgCheck.isChecked();
            if (isChecked && mContext.selectPath.size() >= mContext.pickerMaxCount) {
                Toast.makeText(mContext.getContext(), "您最多只能选择" + mContext.pickerMaxCount + "张照片", Toast.LENGTH_SHORT).show();
                return;
            }

            imgCheck.setChecked(isChecked);
            int position = getLayoutPosition() - (showCamera ? 1 : 0);
            viewShadow.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            if (isChecked && !mContext.selectPath.contains(pictureList.get(position).path)) {
                mContext.selectPath.add(pictureList.get(position).path);
            } else if (!isChecked && mContext.selectPath.contains(pictureList.get(position).path)) {
                mContext.selectPath.remove(pictureList.get(position).path);
            }
            mContext.refreshConfirm();
        }
    }
}
