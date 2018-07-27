package com.xiaofu.lib_base_xiaofu.fancy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiaofu.lib_base_xiaofu.R;


/**
 * Author:          小夫
 * Date:            2017/9/25 10:17
 * Description:     带样式和Hover效果的按钮
 * <p>
 * 大人者，言不必信，行不必果，惟义所在
 */

public class FancyButton extends View {
    // 3种外形模式：圆角矩形、圆弧、直角矩形
    public final static int SHAPE_ROUND_RECT = 101;
    public final static int SHAPE_ARC = 102;
    public final static int SHAPE_RECT = 103;
    // 显示外形
    private int mTagShape;
    // 画笔
    private Paint mPaint;
    // 背景色
    private int mBgColor;
    // 边框颜色
    private int mBorderColor;
    // 字体颜色
    private int mTextColor;
    // 字体按压颜色
    private int mPressedTextColor;
    // 字体大小
    private int mTextSize;
    // 按压状态颜色
    private int mPressedColor;
    // 不可点击颜色
    private int mDisabledColor;
    // 边框宽度
    private int mBorderWidth;
    // 圆角半径
    private float mRadius;
    // 内容
    private String mText;
    // 背景矩形
    private RectF mRectF;
    // 字体边界
    private Rect mTextBound;
    // 按压状态
    private boolean isPressed;
    // 点击状态
    private boolean isClickable;
    // 是否显示边框
    private boolean showBorder;
    // 是否加粗文字
    private boolean isTextBold;
    // 测量边界
    private Rect mBounds;

    public FancyButton(Context context) {
        this(context, null);
    }

    public FancyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        isClickable = true;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        mTextBound = new Rect();
        mBounds = new Rect();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FancyButton);
            mTagShape = a.getInteger(R.styleable.FancyButton_fb_shape, FancyButton.SHAPE_RECT);
            mBgColor = a.getColor(R.styleable.FancyButton_fb_bg_color, Color.parseColor("#ffffff"));
            mBorderColor = a.getColor(R.styleable.FancyButton_fb_border_color, Color.parseColor("#333333"));
            mTextColor = a.getColor(R.styleable.FancyButton_fb_text_color, Color.parseColor("#333333"));
            mPressedColor = a.getColor(R.styleable.FancyButton_fb_hover_color, Color.parseColor("#20000000"));
            mDisabledColor = a.getColor(R.styleable.FancyButton_fb_disabled_color, Color.parseColor("#20000000"));
            mPressedTextColor = a.getColor(R.styleable.FancyButton_fb_text_hover_color, Color.WHITE);
            mBorderWidth = (int) a.getDimension(R.styleable.FancyButton_fb_border_width, MeasureHelperKt.dip2px(context, 1));
            mRadius = (int) a.getDimension(R.styleable.FancyButton_fb_border_radius, MeasureHelperKt.dip2px(context, 5));
            mTextSize = (int) a.getDimension(R.styleable.FancyButton_fb_text_size, MeasureHelperKt.dip2px(context, 14));
            isClickable = a.getBoolean(R.styleable.FancyButton_fb_clickable, true);
            showBorder = a.getBoolean(R.styleable.FancyButton_fb_showBorder, false);
            isTextBold = a.getBoolean(R.styleable.FancyButton_fb_text_bold, false);
            mText = a.getString(R.styleable.FancyButton_fb_text);
            if (mText == null) {
                mText = "";
            }
            a.recycle();
        }
        setClickable(isClickable);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 设置矩形边框
        if (showBorder)
            mRectF.set(mBorderWidth, mBorderWidth, w - mBorderWidth, h - mBorderWidth);
        else
            mRectF.set(0, 0, w, h);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 圆角
        float radius = mRadius;
        if (mTagShape == SHAPE_RECT) {
            radius = 0;
        } else if (mTagShape == SHAPE_ARC) {
            radius = mRectF.height() / 2;
        }

        // 绘制背景
        if (!isClickable) {
            setClickable(false);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mDisabledColor);
            canvas.drawRoundRect(mRectF, radius, radius, mPaint);
        } else {
            if (isPressed) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mPressedColor);
                canvas.drawRoundRect(mRectF, radius, radius, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mBgColor);
                canvas.drawRoundRect(mRectF, radius, radius, mPaint);
            }
        }

        // 绘制边框
        if (showBorder) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderWidth);
            canvas.drawRoundRect(mRectF, radius, radius, mPaint);
        }

        // 绘制文字
        mPaint.setStyle(Paint.Style.FILL);
        if (isPressed) {
            mPaint.setColor(mPressedTextColor);
        } else {
            mPaint.setColor(mTextColor);
        }
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        mPaint.setFakeBoldText(isTextBold);
        int middle = (mTextBound.top + mTextBound.bottom) / 2;// 文字高度，用于修正位置
        canvas.drawText(mText, getWidth() / 2, getHeight() / 2 - middle, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
            float textWidth = mBounds.width();
            width = (int) (getPaddingLeft() + 60 + textWidth + getPaddingRight() + mBorderWidth * 2);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
            float textWidth = mBounds.height();
            height = (int) (getPaddingTop() + 40 + textWidth + getPaddingBottom() + mBorderWidth * 2);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * ====================触摸控制====================
     */

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        isClickable = clickable;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isClickable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressed = true;
                    invalidate();
                    return super.onTouchEvent(event);
                case MotionEvent.ACTION_MOVE:
                    if (isPressed && !_isViewUnder(event.getX(), event.getY())) {
                        isPressed = false;
                        invalidate();
                    }
                    return super.onTouchEvent(event);
                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_CANCEL:
                    if (isPressed) {
                        isPressed = false;
                        invalidate();
                    }
                    return super.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean _isViewUnder(float x, float y) {
        return x >= 0 && x < getWidth() &&
                y >= 0 && y < getHeight();
    }

    public void setText(String msg) {
        mText = msg;
        invalidate();
    }

    public String getTextString() {
        return mText;
    }

}

