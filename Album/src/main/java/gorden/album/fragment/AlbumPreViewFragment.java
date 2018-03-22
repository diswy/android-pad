package gorden.album.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gorden.album.AlbumPickerActivity;
import gorden.album.R;
import gorden.album.adapter.FragmentPagerAdapter;
import gorden.album.utils.AnimationUtils;
import gorden.album.widget.ZoomOutPageTransformer;

import static android.app.Activity.RESULT_OK;
import static gorden.album.AlbumPicker.EXTRA_MAX_COUNT;
import static gorden.album.AlbumPicker.EXTRA_PREVIEW_MODE;
import static gorden.album.AlbumPicker.EXTRA_SELECT_MODE;
import static gorden.album.AlbumPicker.KEY_DEL_IMAGES;
import static gorden.album.AlbumPicker.KEY_IMAGES;
import static gorden.album.fragment.AlbumPickerFragment.MULTI_SELECT_MODE;
import static gorden.album.fragment.AlbumPickerFragment.SINGLE_SELECT_MODE;

/**
 * 预览界面
 */

public class AlbumPreViewFragment extends Fragment implements View.OnClickListener {
    public static final String KEY_PREVIEW_POSITION = "KEY_PREVIEW_POSITION";
    public static final String KEY_PREVIEW_IMAGELIST = "KEY_PREVIEW_IMAGELIST";
    public static final String KEY_PREVIEW_SELECTED = "KEY_PREVIEW_SELECTED";

    public static final int MODE_ALBUM_PREVIEW = 701;//相册预览
    public static final int MODE_ALBUM_DELETE = 702;//图片列表删除
    public static final int MODE_ONLY_PREVIEW = 703;//仅仅预览

    private int previewMode = MODE_ALBUM_PREVIEW;

    private ViewPager pager_image;
    private TextView btn_confirm;
    private ImageButton btn_back;
    private TextView text_position;
    private CheckBox checkbox;
    private View viewClicked;

    private View rootView;
    private ArrayList<String> imgList = new ArrayList<>();
    private ArrayList<String> selectPath = new ArrayList<>();
    private int pickerMaxCount;
    private int currentPosition;
    private int pickerModel;

    private FragmentPagerAdapter pagerAdapter;

    private boolean show = true;
    private View rel_bottom, rel_top;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_album_preview1, container, false);
            pager_image = (ViewPager) rootView.findViewById(R.id.pager_image);
            btn_confirm = (TextView) rootView.findViewById(R.id.btn_confirm);
            btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
            text_position = (TextView) rootView.findViewById(R.id.text_position);
            checkbox = (CheckBox) rootView.findViewById(R.id.checkbox);
            viewClicked = rootView.findViewById(R.id.viewClicked);
            rel_top = rootView.findViewById(R.id.rel_top);
            rel_bottom = rootView.findViewById(R.id.rel_bottom);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();

        previewMode = arguments.getInt(EXTRA_PREVIEW_MODE);
        currentPosition = getArguments().getInt(KEY_PREVIEW_POSITION, 0);
        imgList = getArguments().getStringArrayList(KEY_PREVIEW_IMAGELIST);

        //保证数据正常
        if (imgList == null || imgList.size() == 0) return;
        if (currentPosition > imgList.size()-1) currentPosition = 0;

        pager_image.setPageTransformer(false, new ZoomOutPageTransformer());
        pager_image.setAdapter(pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), imgList));
        pager_image.setCurrentItem(currentPosition);
        ((AlbumPickerActivity) getActivity()).applyBackground(imgList.get(currentPosition));
        text_position.setText((currentPosition + 1) + "/" + imgList.size());

        switch (previewMode) {
            case MODE_ALBUM_PREVIEW://相册
                ArrayList<String> tempSelect = getArguments().getStringArrayList(KEY_PREVIEW_SELECTED);
                if (tempSelect != null) selectPath.addAll(tempSelect);

                pickerMaxCount = getArguments().getInt(EXTRA_MAX_COUNT);
                pickerModel = getArguments().getInt(EXTRA_SELECT_MODE);

                refreshConfirm();
                checkbox.setChecked(selectPath.contains(imgList.get(currentPosition)));
                break;
            case MODE_ALBUM_DELETE://图片选择删除
                btn_confirm.setText("删除");
                btn_confirm.setEnabled(true);
                rel_bottom.setVisibility(View.GONE);
                break;
            case MODE_ONLY_PREVIEW://仅仅预览
                btn_confirm.setVisibility(View.GONE);
                rel_bottom.setVisibility(View.GONE);
                break;
        }

        pager_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                text_position.setText((position + 1) + "/" + imgList.size());
                ((AlbumPickerActivity) getActivity()).applyBackground(imgList.get(position));
                if (pickerModel == MULTI_SELECT_MODE)
                    checkbox.setChecked(selectPath.contains(imgList.get(position)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (previewMode == MODE_ALBUM_PREVIEW || previewMode == MODE_ALBUM_DELETE) {
            btn_confirm.setOnClickListener(this);
        }

        if (previewMode == MODE_ALBUM_PREVIEW && pickerModel == MULTI_SELECT_MODE) {
            viewClicked.setOnClickListener(this);
        }

        btn_back.setOnClickListener(this);
    }

    /**
     * 刷新完成按钮
     */
    public void refreshConfirm() {
        if (pickerModel == SINGLE_SELECT_MODE) {
            btn_confirm.setText("确定");
            btn_confirm.setEnabled(true);
            rel_bottom.setVisibility(View.GONE);
        } else {
            btn_confirm.setText(selectPath.size() > 0 && pickerMaxCount > 1 ? "完成(" + selectPath.size() + "/" + pickerMaxCount + ")" : "完成");
            btn_confirm.setEnabled(selectPath.size() > 0);
        }
    }

    public void onBackPressed(){
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else{
            if (previewMode == MODE_ALBUM_DELETE && selectPath.size()>0){
                Intent resultData = new Intent();
                resultData.putStringArrayListExtra(KEY_DEL_IMAGES, selectPath);
                getActivity().setResult(RESULT_OK, resultData);
            }
            getActivity().finish();
        }
    }

    public void toggleToolbarVisibleState() {
        show = !show;
        if (show) {
            AnimationUtils.visibleViewByAlpha(rel_top);
            if (pickerModel != SINGLE_SELECT_MODE && previewMode == MODE_ALBUM_PREVIEW)
                AnimationUtils.visibleViewByAlpha(rel_bottom);
        } else {
            AnimationUtils.goneViewByAlpha(rel_top);
            AnimationUtils.goneViewByAlpha(rel_bottom);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {//返回按钮
            onBackPressed();
        } else if (v.getId() == R.id.btn_confirm) {//确认
            if (previewMode == MODE_ALBUM_DELETE){//编辑删除模式
                if (imgList.size()>0){
                    selectPath.add(imgList.get(currentPosition));
                    imgList.remove(currentPosition);
                    pagerAdapter.notifyDataSetChanged();
                    text_position.setText((currentPosition + 1) + "/" + imgList.size());
                }
                if (imgList.size()==0){
                    text_position.setText("0/0");
                    Intent resultData = new Intent();
                    resultData.putStringArrayListExtra(KEY_DEL_IMAGES, selectPath);
                    getActivity().setResult(RESULT_OK, resultData);
                    getActivity().finish();
                }
            }else{//相册选择模式
                Intent resultData = new Intent();
                //单选模式,返回当前图片
                if (pickerModel == SINGLE_SELECT_MODE) {
                    selectPath.clear();
                    selectPath.add(imgList.get(currentPosition));
                }
                resultData.putStringArrayListExtra(KEY_IMAGES, selectPath);
                getActivity().setResult(RESULT_OK, resultData);
                getActivity().finish();
            }
        } else if (v.getId() == R.id.viewClicked) {
            boolean isChecked = !checkbox.isChecked();
            if (isChecked && selectPath.size() >= pickerMaxCount) {
                Toast.makeText(getActivity(), "您最多只能选择" + pickerMaxCount + "张照片", Toast.LENGTH_SHORT).show();
                return;
            }
            checkbox.setChecked(isChecked);

            if (isChecked && !selectPath.contains(imgList.get(currentPosition))) {
                selectPath.add(imgList.get(currentPosition));
            } else if (!isChecked && selectPath.contains(imgList.get(currentPosition))) {
                selectPath.remove(imgList.get(currentPosition));
            }
            ((AlbumPickerActivity) getActivity()).refreshSelected(selectPath);
            refreshConfirm();
        }
    }
}
