package com.wuhangjia.firstlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wuhangjia.firstlib.R;
import com.wuhangjia.firstlib.utils.MeasureUtils;

/**
 * Author:          小夫
 * Date:            2017/11/9 12:56
 * Description:     带标签的TextView
 * <p>
 * 大人者，言不必信，行不必果，惟义所在
 */

public class TagTextView extends TextView {

    // 标签背景画笔
    private Paint mTagPaint;
    // 标签文字画笔
    private Paint mTagTextPaint;
    // 标签文字
    private String mTagText;
    // 标签文字大小
    private int mTagTextSize;
    // 标签文字颜色
    private int mTagTextColor;
    // 标签和文字间距
    private int mGap;
    // Tag使用背景图
    private int mTagBj;
    // 标签背景，暂时使用图片形式
    private Bitmap bp;
    // 源文件边界
    private Rect mSrcRect;
    // 目标边界
    private RectF mDstRect;
    // 标签宽度
    private int mTagWidth;
    // 标签内文字边界
    private Rect mTagTextRect;
    // 占位符
    private String space;

    public TagTextView(Context context) {
        this(context, null);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagTextView);
            mTagText = a.getString(R.styleable.TagTextView_ttv_tag_text);
            mTagTextSize = (int) a.getDimension(R.styleable.TagTextView_ttv_tag_text_size, MeasureUtils.sp2px(context, 14f));
            mTagTextColor = a.getColor(R.styleable.TagTextView_ttv_tag_text_color, Color.parseColor("#333333"));
            mGap = (int) a.getDimension(R.styleable.TagTextView_ttv_gap, MeasureUtils.dp2px(context, 0f));
            mTagBj = a.getResourceId(R.styleable.TagTextView_ttv_tag_bg, R.drawable.bg);
            mTagWidth = (int) a.getDimension(R.styleable.TagTextView_ttv_tag_width, -1);
            if (mTagText == null) {
                mTagText = "TAG";
            }
            a.recycle();
        }

        // 去除TextView默认的上线内边距
        super.setIncludeFontPadding(false);

        // 标签背景画笔
        mTagPaint = new Paint();
        mTagPaint.setAntiAlias(true);

        // 标签文字画笔
        mTagTextPaint = new Paint();
        mTagTextPaint.setAntiAlias(true);
        mTagTextPaint.setStyle(Paint.Style.FILL);
        mTagTextPaint.setTextAlign(Paint.Align.CENTER);
        mTagTextPaint.setColor(mTagTextColor);
        mTagTextPaint.setTextSize(mTagTextSize);

        // 文字画笔
        // 内容文字画笔 主要用来测量距离
        Paint mTextPaint = new Paint();
        mTextPaint.setTextSize(getTextSize());

        // 标签计算
        if (mTagWidth == -1) {// 没有给标签宽度的时候默认根据字体大小来给宽度
            mTagWidth = (int) mTagTextPaint.measureText(mTagText, 0, mTagText.length());
            mTagWidth += MeasureUtils.dp2px(context, 10);
        }

        space = " ";
        float spaceWidth = mTextPaint.measureText(space);
        int multiple = (int) ((mTagWidth + mGap) / spaceWidth);
        for (int i = 0; i < multiple; i++) {
            space = space + " ";
        }

        bp = BitmapFactory.decodeResource(getResources(), mTagBj);
        mSrcRect = new Rect(0, 0, bp.getWidth(), bp.getHeight());
        mDstRect = new RectF(0, 0, mTagWidth, getLineHeight());

        // 标签内字体居中修正计算
        mTagTextRect = new Rect();
        mTagTextPaint.getTextBounds(mTagText, 0, mTagText.length(), mTagTextRect);

        super.setText(space + super.getText().toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制标签
        canvas.save();
        canvas.drawBitmap(bp, mSrcRect, mDstRect, mTagPaint);
        canvas.restore();

        // 绘制标签字体
        canvas.save();
        int middle = (mTagTextRect.top + mTagTextRect.bottom) / 2;
        canvas.drawText(mTagText, mTagWidth / 2, getLineHeight() / 2 - middle, mTagTextPaint);
        canvas.restore();
    }

    //-------------------------------代码设置属性-------------------------------
    public void setTagText(String tagText) {
        mTagText = tagText;
        invalidate();
    }

    public void setTagTextSize(int tagTextSize) {
        mTagTextSize = tagTextSize;
        invalidate();
    }

    public void setTagTextColor(int tagTextColor) {
        mTagTextColor = tagTextColor;
        invalidate();
    }

    public void setGap(int gap) {
        mGap = gap;
        invalidate();
    }

    public void setTagBj(int resId) {
        mTagBj = resId;
        invalidate();
    }

    public void setTagWidth(int tagWidth) {
        mTagWidth = tagWidth;
        invalidate();
    }

    public void setText(String content) {
        super.setText(space + content);
    }

}
