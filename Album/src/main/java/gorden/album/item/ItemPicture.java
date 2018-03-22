package gorden.album.item;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import gorden.album.R;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;

/**
 * 列表 item
 * Created by Gorden on 2017/4/2.
 */

public class ItemPicture extends FrameLayout {
    public SketchImageView imgPicture;
    public CheckBox imgCheck;
    public View viewClicked;
    public View viewShadow;

    public ItemPicture(@NonNull Context context, int imgSize) {
        super(context);
        initView(context, imgSize);
    }

    public ItemPicture(@NonNull Context context) {
        super(context);
    }

    public ItemPicture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemPicture(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context, int imgSize) {
        imgPicture = new SketchImageView(context);
        imgPicture.getOptions().setImageDisplayer(new TransitionImageDisplayer())
                .setLoadingImage(R.drawable.album_image_loading).setErrorImage(R.drawable.album_image_error)
                .setResize(imgSize, imgSize).setMaxSize((int) (1.5 * imgSize), (int) (1.5 * imgSize))
                .setThumbnailMode(true).setCacheProcessedImageInDisk(true).setCorrectImageOrientation(true);
        imgPicture.setShowPressedStatus(true);
        addView(imgPicture, imgSize, imgSize);

        viewShadow = new View(context);
        viewShadow.setBackgroundColor(Color.parseColor("#88000000"));
        viewShadow.setVisibility(GONE);
        addView(viewShadow, imgSize, imgSize);

        imgCheck = new CheckBox(context);
        LayoutParams paramsCheckbox = new LayoutParams(dip2px(29), dip2px(29));
        paramsCheckbox.gravity = Gravity.END | Gravity.TOP;
        paramsCheckbox.rightMargin = 5;
        paramsCheckbox.topMargin = 5;
        addView(imgCheck, paramsCheckbox);

        viewClicked = new View(context);
        LayoutParams paramsView = new LayoutParams(dip2px(32), dip2px(32));
        paramsView.gravity = Gravity.END | Gravity.TOP;
        addView(viewClicked, paramsView);

    }

    private int dip2px(int dip) {
        return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
    }
}
