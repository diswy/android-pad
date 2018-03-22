package com.cqebd.student.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;


import com.cqebd.student.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 草稿纸
 * Created by gorden on 2017/10/27.
 */

public class DraftPaperView extends View {
    private Paint mPaint;
    private Paint mEarserPaint;

    private int mType = TYPE_DRAFT_PATH;
    public static final int TYPE_DRAFT_PATH = 140;
    public static final int TYPE_DRAFT_LINE = 141;
    public static final int TYPE_DRAFT_OVAL = 142;
    public static final int TYPE_DRAFT_RECT = 143;

    private int mMode = MODE_PEN;
    public static final int MODE_PEN = 747;
    public static final int MODE_EARSER = 748;

    private DraftPath mDraftPath;
    private List<DraftPath> revokedList;
    private List<DraftPath> restoreList;

    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;

    private AlertDialog dialogClear;
    private AlertDialog dialogPalette;

    public DraftPaperView(Context context) {
        super(context);
        init(context);
    }

    public DraftPaperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DraftPaperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);

        mEarserPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mEarserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEarserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEarserPaint.setStrokeWidth(9);
        mEarserPaint.setStyle(Paint.Style.STROKE);
        mEarserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        revokedList = new ArrayList<>();
        restoreList = new ArrayList<>();

        dialogClear = new AlertDialog.Builder(context)
                .setTitle("确定要清空当前内容，将不可恢复")
                .setPositiveButton("确定", (dialog, which) -> {
                    cacheCanvas.drawPaint(mEarserPaint);
                    revokedList.clear();
                    restoreList.clear();
                    mDraftPath = null;
                    if (onDrawCallBack != null) {
                        onDrawCallBack.revokedSize(revokedList.size());
                        onDrawCallBack.restoreSize(restoreList.size());
                    }
                    invalidate();
                })
                .setNegativeButton("取消", null)
                .create();

        View paletteView = LayoutInflater.from(context).inflate(R.layout.dialog_palette, null);
        dialogPalette = new AlertDialog.Builder(context, R.style.dialog_tran)
                .setView(paletteView)
                .create();
        ((RadioGroup) paletteView.findViewById(R.id.stroke_type_radio_group))
                .setOnCheckedChangeListener((group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.stroke_type_rbtn_draw:
                            setPenType(TYPE_DRAFT_PATH);
                            break;
                        case R.id.stroke_type_rbtn_line:
                            setPenType(TYPE_DRAFT_LINE);
                            break;
                        case R.id.stroke_type_rbtn_circle:
                            setPenType(TYPE_DRAFT_OVAL);
                            break;
                        case R.id.stroke_type_rbtn_rectangle:
                            setPenType(TYPE_DRAFT_RECT);
                            break;
                    }
                });
        ((SeekBar) paletteView.findViewById(R.id.stroke_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ((RadioGroup) paletteView.findViewById(R.id.stroke_color_radio_group))
                .setOnCheckedChangeListener((group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.stroke_color_black:
                            mPaint.setColor(Color.BLACK);
                            break;
                        case R.id.stroke_color_red:
                            mPaint.setColor(Color.RED);
                            break;
                        case R.id.stroke_color_green:
                            mPaint.setColor(Color.GREEN);
                            break;
                        case R.id.stroke_color_orange:
                            mPaint.setColor(Color.parseColor("#ffbb33"));
                            break;
                        case R.id.stroke_color_blue:
                            mPaint.setColor(Color.BLUE);
                            break;
                    }
                });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        if (cacheBitmap == null) {
            cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            cacheCanvas = new Canvas(cacheBitmap);
        }

        if (mDraftPath != null)
            mDraftPath.draw(cacheCanvas);

        canvas.drawBitmap(cacheBitmap, 0, 0, null);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int touchMode = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchMode = 0;
                performClick();
                if (mMode == MODE_PEN)
                    mDraftPath = new DraftPath(event.getX(), event.getY(), mPaint, mType);
                else
                    mDraftPath = new DraftPath(event.getX(), event.getY(), mEarserPaint, TYPE_DRAFT_PATH);
                break;

            case MotionEvent.ACTION_MOVE:
                touchMode = 1;
                mDraftPath.move(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (touchMode == 1 && (mMode == MODE_PEN || revokedList.size() > 0)) {
                    restoreList.clear();
                    revokedList.add(mDraftPath);
                    if (onDrawCallBack != null) {
                        onDrawCallBack.revokedSize(revokedList.size());
                        onDrawCallBack.restoreSize(restoreList.size());
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 资源释放
     */
    public void recycle() {
        if (cacheBitmap != null) {
            cacheBitmap.recycle();
            cacheBitmap = null;
            cacheCanvas = null;
        }
        restoreList.clear();
        revokedList.clear();
        restoreList = null;
        revokedList = null;
        mDraftPath = null;
        System.gc();
        invalidate();
    }

    /**
     * 删除一笔
     */
    public void revoked() {
        if (revokedList.size() > 0) {
            restoreList.add(revokedList.remove(revokedList.size() - 1));
            cacheCanvas.drawPaint(mEarserPaint);
            mDraftPath = null;
            for (DraftPath draftPath : revokedList) {
                if (mType == TYPE_DRAFT_PATH)
                    draftPath.draw(cacheCanvas);
                else
                    cacheCanvas.drawPath(draftPath.draftPath, draftPath.paint);
            }
            if (onDrawCallBack != null) {
                onDrawCallBack.revokedSize(revokedList.size());
                onDrawCallBack.restoreSize(restoreList.size());
            }
            invalidate();
        }
    }

    /**
     * 恢复一笔
     */
    public void restore() {
        if (restoreList.size() > 0) {
            revokedList.add(restoreList.remove(restoreList.size() - 1));
            cacheCanvas.drawPaint(mEarserPaint);
            mDraftPath = null;
            for (DraftPath draftPath : revokedList) {
                if (mType == TYPE_DRAFT_PATH)
                    draftPath.draw(cacheCanvas);
                else
                    cacheCanvas.drawPath(draftPath.draftPath, draftPath.paint);
            }
            if (onDrawCallBack != null) {
                onDrawCallBack.revokedSize(revokedList.size());
                onDrawCallBack.restoreSize(restoreList.size());
            }
            invalidate();
        }
    }

    /**
     * 清空内容
     */
    public void clear() {
        if (revokedList.size() > 0) {
            dialogClear.show();
        }
    }

    /**
     * 设置绘制类型
     *
     * @param type TYPE_DRAFT_PATH
     */
    public void setPenType(int type) {
        this.mType = type;
    }

    /**
     * 画笔模式
     */
    public void modePen() {
        mMode = MODE_PEN;
    }

    /**
     * 橡皮擦模式
     */
    public void modeEarser() {
        mMode = MODE_EARSER;
    }

    /**
     * @return 返回当前绘制模式
     */
    public int currentMode() {
        return mMode;
    }

    /**
     * @param width 0-100
     */
    public void setStrokeWidth(float width) {
        float strokeWidth = width / 10 * 3 + 3;
        mPaint.setStrokeWidth(strokeWidth);
        mEarserPaint.setStrokeWidth(3 * strokeWidth);
    }

    public void showPalette() {
        dialogPalette.show();
    }

    private OnDrawCallBack onDrawCallBack;

    public void setOnDrawCallBack(OnDrawCallBack onDrawCallBack) {
        this.onDrawCallBack = onDrawCallBack;
    }

    public interface OnDrawCallBack {
        void revokedSize(int size);

        void restoreSize(int size);
    }

    /**
     * 草稿绘制信息
     */
    private class DraftPath {
        private float startX;
        private float startY;
        private Paint paint;
        private int pathType;
        private Path draftPath;

        DraftPath(float startX, float startY, Paint paint, int type) {
            this.startX = startX;
            this.startY = startY;
            this.paint = new Paint(paint);
            this.pathType = type;
            draftPath = new Path();
            draftPath.moveTo(startX, startY);
        }

        void move(float x, float y) {
            switch (pathType) {
                case TYPE_DRAFT_PATH:
                    draftPath.quadTo(startX, startY, (startX + x) / 2, (startY + y) / 2);
                    startX = x;
                    startY = y;
                    break;
                case TYPE_DRAFT_LINE:
                    draftPath.reset();
                    draftPath.moveTo(startX, startY);
                    draftPath.lineTo(x, y);
                    break;
                case TYPE_DRAFT_OVAL:
                    draftPath.reset();
                    draftPath.addOval(new RectF(startX, startY, x, y), Path.Direction.CCW);
                    break;
                case TYPE_DRAFT_RECT:
                    draftPath.reset();
                    draftPath.addRect(new RectF(startX, startY, x, y), Path.Direction.CCW);
                    break;
            }

        }

        void draw(Canvas canvas) {
            if (pathType != TYPE_DRAFT_PATH) {
                canvas.drawPaint(mEarserPaint);
                for (DraftPath draftPath : revokedList) {
                    canvas.drawPath(draftPath.draftPath, draftPath.paint);
                }
            }
            canvas.drawPath(draftPath, paint);
        }
    }
}
