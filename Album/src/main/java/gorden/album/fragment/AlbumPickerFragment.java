package gorden.album.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gorden.album.AlbumPicker;
import gorden.album.AlbumPickerActivity;
import gorden.album.R;
import gorden.album.adapter.DirAdapter;
import gorden.album.adapter.GridItemDecoration;
import gorden.album.adapter.PictureAdapter;
import gorden.album.entity.Picture;
import gorden.album.entity.PictureDirectory;
import gorden.album.utils.PermissionsConstant;
import gorden.album.utils.PictureScanner;
import gorden.album.utils.SingleMediaScanner;
import me.xiaopan.sketch.util.SketchUtils;

import static android.app.Activity.RESULT_OK;
import static gorden.album.AlbumPicker.EXTRA_GRID_COLUMN;
import static gorden.album.AlbumPicker.EXTRA_MAX_COUNT;
import static gorden.album.AlbumPicker.EXTRA_PREVIEW_ENABLED;
import static gorden.album.AlbumPicker.EXTRA_SELECTED_PATH;
import static gorden.album.AlbumPicker.EXTRA_SELECT_MODE;
import static gorden.album.AlbumPicker.EXTRA_SHOW_CAMERA;
import static gorden.album.AlbumPicker.EXTRA_SHOW_GIF;
import static gorden.album.AlbumPicker.KEY_IMAGES;
import static gorden.album.AlbumPicker.REQUEST_CAMERA;
import static gorden.album.AlbumPicker.mCurrentPhotoPath;

/**
 * 图片列表
 * Created by Gorden on 2017/4/4.
 */

public class AlbumPickerFragment extends Fragment implements View.OnClickListener {
    public static final int SINGLE_SELECT_MODE = 701;
    public static final int MULTI_SELECT_MODE = 702;


    public final static int DEFAULT_MAX_COUNT = 9;//最大选择数量
    public final static int DEFAULT_COLUMN_NUMBER = 3;//相册列宽

    public int pickerMaxCount = DEFAULT_MAX_COUNT;//多选模式下选择数量
    public int pickerGridColumn = DEFAULT_COLUMN_NUMBER;//列宽
    public int pickerModel = SINGLE_SELECT_MODE;

    private boolean showGif = true;//默认显示Gif
    private boolean showCamera = true;//默认显示相机
    private boolean previewEnabled = true;//支持预览

    public ArrayList<String> selectPath;//已选择的图片

    protected BottomSheetBehavior dirBehavior;
    protected View view_shadow;//阴影
    protected RecyclerView recyclerDir;//相册目录
    protected TextView textDirName;

    protected RecyclerView recyclerAlbum;//相片

    public TextView btn_confirm;
    public TextView btn_preview;

    private String backgroundPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * 初始化配置参数
         */
        receiveParameters();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_picker1, container, false);
        bindInitView(rootView);
        initDir();
        return rootView;
    }

    /**
     * 初始化布局控件
     */
    private void bindInitView(View rootView) {
        view_shadow = rootView.findViewById(R.id.view_shadow);
        recyclerDir = (RecyclerView) rootView.findViewById(R.id.recycler_dir);
        dirBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.behaviorView));
        dirBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        dirBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    view_shadow.setClickable(false);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_EXPANDED) {
                    view_shadow.setClickable(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                slideOffset = Float.valueOf(slideOffset).isNaN() ? 0 : slideOffset;
                recyclerDir.setTranslationX(appWidth() * slideOffset);
                view_shadow.setAlpha((1 + slideOffset) * 0.7f);
            }
        });

        textDirName = (TextView) rootView.findViewById(R.id.textDirName);

        recyclerAlbum = (RecyclerView) rootView.findViewById(R.id.recycler_album);
        recyclerAlbum.setLayoutManager(new GridLayoutManager(getContext(), pickerGridColumn));
        recyclerAlbum.addItemDecoration(new GridItemDecoration());
        ((SimpleItemAnimator) recyclerAlbum.getItemAnimator()).setSupportsChangeAnimations(false);

        btn_confirm = (TextView) rootView.findViewById(R.id.btn_confirm);
        btn_preview = (TextView) rootView.findViewById(R.id.btn_preview);
        ImageButton btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);

        view_shadow.setOnClickListener(this);
        view_shadow.setClickable(false);
        btn_confirm.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
        textDirName.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        if (!previewEnabled) btn_preview.setVisibility(View.GONE);
        if (pickerModel == SINGLE_SELECT_MODE){
            btn_confirm.setVisibility(View.GONE);
            btn_preview.setVisibility(View.GONE);
        }
        refreshConfirm();
    }

    /**
     * 接收初始参数
     */
    private void receiveParameters() {
        Bundle parameterIntent = getArguments();
        showCamera = parameterIntent.getBoolean(EXTRA_SHOW_CAMERA, true);
        showGif = parameterIntent.getBoolean(EXTRA_SHOW_GIF, true);
        previewEnabled = parameterIntent.getBoolean(EXTRA_PREVIEW_ENABLED, true);
        pickerModel = parameterIntent.getInt(EXTRA_SELECT_MODE, SINGLE_SELECT_MODE);
        pickerMaxCount = parameterIntent.getInt(EXTRA_MAX_COUNT, pickerModel == SINGLE_SELECT_MODE ? 1 : DEFAULT_MAX_COUNT);
        pickerGridColumn = parameterIntent.getInt(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);

        selectPath = parameterIntent.getStringArrayList(EXTRA_SELECTED_PATH);
        if (selectPath == null) selectPath = new ArrayList<>();
    }

    /**
     * 初始化相册目录
     */
    private void initDir() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerDir.setLayoutManager(layoutManager);
        recyclerDir.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) recyclerDir.getItemAnimator()).setSupportsChangeAnimations(false);

        PictureScanner.getInstance((AppCompatActivity) getActivity()).scan(new PictureScanner.OnPicturesLoadedListener() {
            @Override
            public void onPicturesLoaded(List<PictureDirectory> directories) {
                calculateDirHeight(directories.size());

                recyclerDir.setAdapter(new DirAdapter(AlbumPickerFragment.this, directories));

                if (directories.size() > 0) {
                    textDirName.setText(directories.get(0).dirName);
                    loadPicture(directories.get(0).pictures, true);
                }else{
                    recyclerAlbum.setAdapter(new PictureAdapter(AlbumPickerFragment.this, null, showCamera));
                }
            }
        }, showGif);

    }

    /**
     * 计算dir高度
     */
    private void calculateDirHeight(int count) {
        int itemHeight = SketchUtils.dp2px(getActivity(), 100);
        if (itemHeight * count > appHeight() / 3 * 2) {
            recyclerDir.getLayoutParams().height = appHeight() / 3 * 2;
            dirBehavior.setPeekHeight(appHeight() / 3 * 2);
            recyclerDir.requestLayout();
        } else {
            dirBehavior.setPeekHeight(itemHeight * count);
        }
    }

    /**
     * 开关相册目录
     */
    public void toggleDir() {
        if (dirBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            dirBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            dirBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void notifySelected() {
        refreshConfirm();
        recyclerAlbum.getAdapter().notifyDataSetChanged();
    }

    /**
     * 切换相册目录
     */
    public void onPictureDirectorySelected(PictureDirectory directory, boolean showCamera) {
        textDirName.setText(directory.dirName);
        loadPicture(directory.pictures, showCamera);
    }

    /**
     * 加载相册图片
     *
     * @param pictureList 图片集合
     */
    private void loadPicture(List<Picture> pictureList, boolean showCamera) {
        backgroundPath = pictureList.get(0).path;
        ((AlbumPickerActivity) getActivity()).applyBackground(backgroundPath);
        recyclerAlbum.setAdapter(new PictureAdapter(this, pictureList, showCamera && this.showCamera));
    }

    /**
     * 刷新完成按钮
     */
    public void refreshConfirm() {
        btn_confirm.setText(selectPath.size() > 0 && pickerMaxCount > 1 ?
                String.format(getString(R.string.album_str_complete), "(" + selectPath.size() + "/" + pickerMaxCount + ")") : String.format(getString(R.string.album_str_complete), ""));
        btn_confirm.setEnabled(selectPath.size() > 0);
        btn_confirm.setTextColor(ContextCompat.getColor(getContext(), (selectPath.size() > 0 ? R.color.album_btn_textcolor : R.color.album_btn_textcolor_e)));

        btn_preview.setEnabled(selectPath.size() > 0);
        btn_preview.setTextColor(ContextCompat.getColor(getContext(), (selectPath.size() > 0 ? R.color.album_btn_textcolor : R.color.album_btn_textcolor_e)));

    }

    /**
     * 完成选择
     */
    public void confirm() {
        Intent resultData = new Intent();
        resultData.putStringArrayListExtra(KEY_IMAGES, selectPath);
        getActivity().setResult(RESULT_OK, resultData);
        getActivity().finish();
    }

    /**
     * 预览图片
     *
     * @param position current position
     */
    public void preViewImage(List<Picture> pictures, int position) {
        ArrayList<String> pathList = new ArrayList<>();
        for (Picture picture : pictures) {
            pathList.add(picture.path);
        }
        ((AlbumPickerActivity) getActivity()).preViewAlbum(pathList, selectPath, position);
    }

    /**
     * 预览图片 当前选择的
     */
    public void preViewImage() {
        ((AlbumPickerActivity) getActivity()).preViewAlbum(selectPath, selectPath, 0);
    }

    public int appWidth() {
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.width();
    }

    private int appHeight() {
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return rect.height();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(mCurrentPhotoPath))
                new SingleMediaScanner(getContext()).scanFile(mCurrentPhotoPath);//刷新媒体库
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsConstant.REQUEST_CAMERA) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), R.string.album_str_camera_hint, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        AlbumPicker.builder().openCamera(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            confirm();
        } else if (id == R.id.textDirName || id == R.id.view_shadow) {
            toggleDir();
        } else if (id == R.id.btn_preview) {
            preViewImage();
        } else if (id == R.id.btn_back) {
            getActivity().finish();
        }
    }

    /**
     * 刷新背景
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((AlbumPickerActivity) getActivity()).applyBackground(backgroundPath);
        }
    }
}
