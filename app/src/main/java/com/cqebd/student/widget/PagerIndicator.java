package com.cqebd.student.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.cqebd.student.R;


/**
 * viewpager 指示器
 * Created by Gorden on 2016/4/24.
 */
public class PagerIndicator extends View {
    private static final int DEFAULT_SELECTED_COLOR = 0xFF0000;
    private static final int DEFAULT_UNSELECTED_COLOR = 0xCCCCCC;
    private static final int DEFAULT_RADIU = 20;
    private static final int DEFAULT_PADDING = 10;

    private int indexCount;
    private int indexPosition;
    private float mRadiu = 20;
    private float mPaddng = 10;
    private int selectColor;
    private int unSelectColor;

    private Drawable selectDrawable;
    private Drawable unSelectDrawable;


    private Paint indicatorPaint;
    private Paint numPaint;


    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator);
            mRadiu = array.getDimension(R.styleable.PagerIndicator_circle_radiu, DEFAULT_RADIU);
            mPaddng = array.getDimension(R.styleable.PagerIndicator_circle_padding, DEFAULT_PADDING);
            selectColor = array.getColor(R.styleable.PagerIndicator_select_color, DEFAULT_SELECTED_COLOR);
            unSelectColor = array.getColor(R.styleable.PagerIndicator_unselect_color, DEFAULT_UNSELECTED_COLOR);
            selectDrawable = array.getDrawable(R.styleable.PagerIndicator_select_drawable);
            unSelectDrawable = array.getDrawable(R.styleable.PagerIndicator_unselect_drawable);
            array.recycle();
        }

        init();
    }

    private void init() {
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numPaint.setStyle(Paint.Style.STROKE);
        numPaint.setTextAlign(Paint.Align.CENTER);
        numPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float drawWidth = mRadiu * 2 * indexCount + mPaddng * (indexCount - 1);
        float startX = (getWidth() - drawWidth) / 2 + mRadiu;
        float startY = getHeight() / 2;

        indicatorPaint.setColor(unSelectColor);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        numPaint.setColor(selectColor);
        for (int i = 0; i < indexCount; i++) {
            if (null == unSelectDrawable)
                canvas.drawCircle(startX + (2 * mRadiu + mPaddng) * i, startY, mRadiu, indicatorPaint);
            else {
                BitmapDrawable bd = (BitmapDrawable) unSelectDrawable;
                Rect fromRect = new Rect(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
                RectF toRect = new RectF(startX + (2 * mRadiu + mPaddng) * i - mRadiu, startY - mRadiu, startX + (2 * mRadiu + mPaddng) * i + mRadiu, startY + mRadiu);
                canvas.drawBitmap(bd.getBitmap(), fromRect, toRect, indicatorPaint);
                bd.setCallback(null);
            }
            Rect rect = new Rect();
            numPaint.getTextBounds(String.valueOf(i + 1), 0, 1, rect);
            canvas.drawText(String.valueOf(i + 1), startX + (2 * mRadiu + mPaddng) * i, startY + rect.height() / 2, numPaint);
        }
        indicatorPaint.setColor(selectColor);
        indicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (indexCount > 0) {
            if (null == selectDrawable) {
                canvas.drawCircle(startX + (2 * mRadiu + mPaddng) * indexPosition, startY, mRadiu, indicatorPaint);
            } else {
                BitmapDrawable bd = (BitmapDrawable) selectDrawable;
                Rect fromRect = new Rect(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
                RectF toRect = new RectF(startX + (2 * mRadiu + mPaddng) * indexPosition - mRadiu, startY - mRadiu, startX + (2 * mRadiu + mPaddng) * indexPosition + mRadiu, startY + mRadiu);
                canvas.drawBitmap(bd.getBitmap(), fromRect, toRect, indicatorPaint);
                bd.setCallback(null);
            }
            Rect rect = new Rect();
            numPaint.getTextBounds(String.valueOf(indexPosition+1), 0, 1, rect);
            numPaint.setColor(unSelectColor);
            canvas.drawText(String.valueOf(indexPosition+1), startX + (2 * mRadiu + mPaddng) * indexPosition, startY + rect.height() / 2, numPaint);
        }
    }

    public void setupWithViewPager(ViewPager pager) {
        indexCount = pager.getAdapter() == null ? 0 : pager.getAdapter().getCount();
        indexPosition = 0;
        pager.addOnPageChangeListener(pageChangeListener);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        if (specMode != MeasureSpec.EXACTLY) {
            height = (int) (mRadiu * 2);
        }
        setMeasuredDimension(width, height);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indexPosition = position;
            invalidate();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void setmRadiu(float mRadiu) {
        this.mRadiu = mRadiu;
        numPaint.setTextSize(1.5f * mRadiu);
    }

    public void setmPaddng(float mPaddng) {
        this.mPaddng = mPaddng;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public void setUnSelectColor(int unSelectColor) {
        this.unSelectColor = unSelectColor;
    }
}
