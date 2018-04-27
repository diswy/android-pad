package com.wuhangjia.firstlib.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wuhangjia.firstlib.R;

import java.util.List;

/**
 * Author:          小夫
 * Date:            2017/11/30 12:38
 * Description:     根据图片数量展示不同效果
 * <p>
 * 大人者，言不必信，行不必果，惟义所在
 */

public class FancyImage extends FrameLayout {
    private View v;
    private Context mContext;
    private int[] images = {R.id.image_1, R.id.image_2, R.id.image_3, R.id.image_4, R.id.image_5, R.id.image_6, R.id.image_7, R.id.image_8, R.id.image_9};

    public FancyImage(@NonNull Context context, List<String> imagePath) {
        super(context);
        this.mContext = context;
        v = initView(imagePath);
        addView(v);
    }

    public FancyImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public FancyImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private View initView(List<String> imgPath) {
        View view;
        switch (imgPath.size()) {
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_1, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 2:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_2, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 3:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_3, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 4:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_4, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 5:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_5, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 6:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_6, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 7:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_7, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 8:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_8, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            case 9:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_9, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_1, null);
                for (int i = 0; i < imgPath.size(); i++)
                    Glide.with(mContext).load(imgPath.get(i)).into((ImageView) view.findViewById(images[i]));
                return view;
        }
    }


}
