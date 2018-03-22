package gorden.album.item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gorden.album.R;
import me.xiaopan.sketch.SketchImageView;

/**
 * document
 * Created by Gordn on 2017/4/1.
 */

public class ItemDir extends LinearLayout{
    public SketchImageView imgDir;
    public TextView textDir;
    public TextView textCount;
    public ImageView viewSelected;

    public ItemDir(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(HORIZONTAL);
        int padding = dip2px(10,context);
        setPadding(padding,padding,padding,padding);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundResource(R.drawable.photo_ic_dir_bg);
        addView(frameLayout,dip2px(80,context),dip2px(80,context));
        frameLayout.setPadding(0,0,dip2px(5,context),dip2px(5,context));

        imgDir = new SketchImageView(context);
        imgDir.setBackgroundColor(Color.LTGRAY);
        imgDir.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgDir.getOptions().setThumbnailMode(true).setResize(dip2px(75, context),dip2px(75, context))
               .setMaxSize(dip2px(150, context),dip2px(150, context)).setCacheProcessedImageInDisk(true)
               .setCorrectImageOrientation(true);
        LayoutParams paramsDir = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(imgDir,paramsDir);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(VERTICAL);
        LayoutParams paramsLin = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        paramsLin.weight=1;
        paramsLin.gravity = Gravity.CENTER_VERTICAL;
        textLayout.setPadding(dip2px(20,context),0,dip2px(20,context),0);
        addView(textLayout,paramsLin);

        textDir = new TextView(context);
        textDir.setLines(1);
        textDir.setTextSize(15);
        textDir.setEllipsize(TextUtils.TruncateAt.END);
        LayoutParams paramsText = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        paramsText.bottomMargin = dip2px(5,context);
        textLayout.addView(textDir,paramsText);

        textCount = new TextView(context);
        textCount.setTextSize(14);
        textCount.setTextColor(Color.LTGRAY);
        textLayout.addView(textCount);

        viewSelected = new ImageView(context);
        viewSelected.setBackgroundResource(R.drawable.album_check);
        LayoutParams paramsSelected = new LayoutParams(dip2px(20,context),dip2px(20,context));
        paramsSelected.rightMargin = 20;
        paramsSelected.gravity= Gravity.CENTER_VERTICAL;
        addView(viewSelected,paramsSelected);
        viewSelected.setVisibility(GONE);

    }

    public static int dip2px(int dip, Context context) {
        return (int) (dip * context.getApplicationContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
