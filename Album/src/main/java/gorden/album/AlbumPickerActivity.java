package gorden.album;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;

import gorden.album.fragment.AlbumPickerFragment;
import gorden.album.fragment.AlbumPreViewFragment;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.OldStateImage;
import me.xiaopan.sketch.util.SketchUtils;

import static gorden.album.AlbumPicker.EXTRA_MAX_COUNT;
import static gorden.album.AlbumPicker.EXTRA_PREVIEW_LIST;
import static gorden.album.AlbumPicker.EXTRA_PREVIEW_MODE;
import static gorden.album.AlbumPicker.EXTRA_PREVIEW_POSITION;
import static gorden.album.AlbumPicker.EXTRA_SELECT_MODE;
import static gorden.album.fragment.AlbumPickerFragment.DEFAULT_MAX_COUNT;
import static gorden.album.fragment.AlbumPickerFragment.SINGLE_SELECT_MODE;
import static gorden.album.fragment.AlbumPreViewFragment.KEY_PREVIEW_IMAGELIST;
import static gorden.album.fragment.AlbumPreViewFragment.KEY_PREVIEW_POSITION;
import static gorden.album.fragment.AlbumPreViewFragment.KEY_PREVIEW_SELECTED;
import static gorden.album.fragment.AlbumPreViewFragment.MODE_ALBUM_DELETE;
import static gorden.album.fragment.AlbumPreViewFragment.MODE_ALBUM_PREVIEW;
import static gorden.album.fragment.AlbumPreViewFragment.MODE_ONLY_PREVIEW;

/**
 * Album图片选择器 页面
 * Created by Gordn on 2017/3/31.
 */

public class AlbumPickerActivity extends AppCompatActivity {
    private AlbumPickerFragment pickerFragment;
    private FragmentManager fragmentManager;

    private SketchImageView backgroundImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_picker);

        backgroundImageView = (SketchImageView) findViewById(R.id.image_main_background);

        initViews();

        fragmentManager = getSupportFragmentManager();

        int previewMode = getIntent().getIntExtra(EXTRA_PREVIEW_MODE, MODE_ALBUM_PREVIEW);

        switch (previewMode) {
            case MODE_ALBUM_PREVIEW://相册
                pickerFragment = new AlbumPickerFragment();
                pickerFragment.setArguments(getIntent().getExtras());
                pickerView();
                break;
            case MODE_ALBUM_DELETE://图片选择删除
                preViewDelete(getIntent().getStringArrayListExtra(EXTRA_PREVIEW_LIST), getIntent().getIntExtra(EXTRA_PREVIEW_POSITION, 0));
                break;
            case MODE_ONLY_PREVIEW://仅仅预览
                preViewOnly(getIntent().getStringArrayListExtra(EXTRA_PREVIEW_LIST), getIntent().getIntExtra(EXTRA_PREVIEW_POSITION, 0));
                break;
        }
    }

    private void initViews() {
        ViewGroup.LayoutParams layoutParams = backgroundImageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        backgroundImageView.setLayoutParams(layoutParams);

        backgroundImageView.getOptions().setLoadingImage(new OldStateImage(new DrawableStateImage(R.drawable.shape_window_background)))
                .setImageProcessor(GaussianBlurImageProcessor.makeLayerColor(Color.parseColor("#66000000")))
                .setCacheProcessedImageInDisk(true)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .setShapeSizeByFixedSize(true)
                .setMaxSize(getResources().getDisplayMetrics().widthPixels / 4,
                        getResources().getDisplayMetrics().heightPixels / 4)
                .setImageDisplayer(new TransitionImageDisplayer(true));
    }

    /**
     * 设置模糊背景
     *
     * @param imgPath 图片地址
     */
    public void applyBackground(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) return;
        Drawable drawable = SketchUtils.getLastDrawable(backgroundImageView.getDrawable());
        if (drawable instanceof SketchDrawable) {
            SketchDrawable sketchDrawable = (SketchDrawable) drawable;
            if (!TextUtils.isEmpty(sketchDrawable.getUri())&&imgPath.contains(sketchDrawable.getUri())) return;//如果地址没变,则不设置
        }
        backgroundImageView.displayImage(imgPath);
    }

    private void pickerView() {
        fragmentManager.beginTransaction().add(R.id.frame_content, pickerFragment).commit();
    }

    /**
     * 相册选择
     */
    public void preViewAlbum(@NonNull ArrayList<String> imglist, ArrayList<String> selected, int position) {
        AlbumPreViewFragment preViewFragment = new AlbumPreViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PREVIEW_POSITION, position);
        bundle.putStringArrayList(KEY_PREVIEW_IMAGELIST, imglist);
        bundle.putStringArrayList(KEY_PREVIEW_SELECTED, selected);
        bundle.putInt(EXTRA_PREVIEW_MODE, MODE_ALBUM_PREVIEW);
        int pickerModel = getIntent().getIntExtra(EXTRA_SELECT_MODE, SINGLE_SELECT_MODE);
        bundle.putInt(EXTRA_SELECT_MODE, pickerModel);
        bundle.putInt(EXTRA_MAX_COUNT, getIntent().getIntExtra(EXTRA_MAX_COUNT, pickerModel == SINGLE_SELECT_MODE ? 1 : DEFAULT_MAX_COUNT));
        preViewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (pickerFragment != null) fragmentTransaction.hide(pickerFragment);
        fragmentManager.popBackStack();
        fragmentTransaction.add(R.id.frame_content, preViewFragment, "preview").commit();
    }

    /**
     * 图片编辑删除
     */
    public void preViewDelete(@NonNull ArrayList<String> imglist, int position) {
        AlbumPreViewFragment preViewFragment = new AlbumPreViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PREVIEW_POSITION, position);
        bundle.putStringArrayList(KEY_PREVIEW_IMAGELIST, imglist);
        bundle.putInt(EXTRA_PREVIEW_MODE, MODE_ALBUM_DELETE);
        preViewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_content, preViewFragment, "preview").commit();
    }

    /**
     * 图片预览
     */
    public void preViewOnly(@NonNull ArrayList<String> imglist, int position) {
        AlbumPreViewFragment preViewFragment = new AlbumPreViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PREVIEW_POSITION, position);
        bundle.putStringArrayList(KEY_PREVIEW_IMAGELIST, imglist);
        bundle.putInt(EXTRA_PREVIEW_MODE, MODE_ONLY_PREVIEW);
        preViewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_content, preViewFragment, "preview").commit();
    }

    public void refreshSelected(ArrayList<String> selectPath) {
        if (pickerFragment != null) {
            pickerFragment.selectPath = selectPath;
            pickerFragment.notifySelected();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getFragments().size() > 0 && fragmentManager.getFragments().get(0) instanceof AlbumPreViewFragment) {
            ((AlbumPreViewFragment) fragmentManager.getFragments().get(0)).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
