package gorden.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;
import android.widget.TextView;

import gorden.lib.R;


/**
 * 刷新group控件
 * Created by gorden on 2016/4/27.
 */
public class NiftyRefreshLayout extends ViewGroup{
    //status enum
    public final static byte Nifty_STATUS_INIT = 1;
    public final static byte Nifty_STATUS_PREPARE = 2;
    public final static byte Nifty_STATUS_LOADING = 3;
    public final static byte Nifty_STATUS_COMPLETE = 4;
    private byte mStatus = Nifty_STATUS_INIT;

    protected final String LOG_TAG = "NiftyRefreshLayout";

    //protected View mContent;
    private static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;
    private static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;
    private static byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 0x01 << 2;
    private static byte FLAG_PIN_CONTENT = 0x01 << 3;
    private static byte MASK_AUTO_REFRESH = 0x03;

    protected View mContent;
    // 可配置xml文件 定义标题和内容
    private int mHeaderId = 0;
    private int mContainerId = 0;

    //多点触控
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private float mLastMotionY;

    // config
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    private View mHeaderView;
    private NiftyUIHandlerHolder mNiftyUIHandlerHolder = NiftyUIHandlerHolder.create();
    private OnNiftyRefreshListener refreshListener;
    // working parameters
    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private int mHeaderHeight;
    private boolean mDisableWhenHorizontalMove = false;
    private int mFlag = 0x00;

    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;

    private MotionEvent mLastMoveEvent;
    private NiftyUIHandlerHook mRefreshCompleteHook;

    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;
    private NiftyIndicator mNiftyIndicator;
    private boolean mHasSendCancelEvent = false;
    private Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performRefreshComplete();
        }
    };

    public NiftyRefreshLayout(Context context) {
        this(context,null);
    }

    public NiftyRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NiftyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mNiftyIndicator = new NiftyIndicator();
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.NiftyRefreshLayout, 0, 0);
        if (arr != null) {

            mHeaderId = arr.getResourceId(R.styleable.NiftyRefreshLayout_nifty_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.NiftyRefreshLayout_nifty_content, mContainerId);

            mNiftyIndicator.setResistance(
                    arr.getFloat(R.styleable.NiftyRefreshLayout_nifty_resistance, mNiftyIndicator.getResistance()));

            mDurationToClose = arr.getInt(R.styleable.NiftyRefreshLayout_nifty_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.NiftyRefreshLayout_nifty_duration_to_close_header, mDurationToCloseHeader);

            float ratio = mNiftyIndicator.getRatioOfHeaderToHeightRefresh();
            ratio = arr.getFloat(R.styleable.NiftyRefreshLayout_nifty_ratio_of_header_height_to_refresh, ratio);
            mNiftyIndicator.setRatioOfHeaderHeightToRefresh(ratio);

            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.NiftyRefreshLayout_nifty_keep_header_when_refresh, mKeepHeaderWhenRefresh);

            mPullToRefresh = arr.getBoolean(R.styleable.NiftyRefreshLayout_nifty_pull_to_fresh, mPullToRefresh);
            arr.recycle();
        }
        mScrollChecker = new ScrollChecker();
        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("NiftyRefreshLayout only can host 2 elements");
        }else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }
            if (mContent == null || mHeaderView == null) {

                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof OnNiftyRefreshListener) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof OnNiftyRefreshListener) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    }
                    // only one is specified
                    else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        }else if (childCount == 1) {
            mContent = getChildAt(0);
        }else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in NiftyRefreshLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        super.onFinishInflate();
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }

        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mNiftyIndicator.setHeaderHeight(mHeaderHeight);
        }
        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
        }
    }
    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
    
    
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offsetX = mNiftyIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
           
        }
        if (mContent != null) {
            if (isPinContent()) {
                offsetX = 0;
            }
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mNiftyIndicator.onRelease();
                if (mNiftyIndicator.hasLeftStartPosition()) {
                    onRelease(false);
                    if (mNiftyIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }

            case MotionEvent.ACTION_DOWN:
                mLastMotionY = e.getY();
                mHasSendCancelEvent = false;
                mNiftyIndicator.onPressDown(e.getX(), e.getY());
                mActivePointerId=e.getPointerId(0);
                mScrollChecker.abortIfWorking();

                mPreventForHorizontal = false;
                // The cancel event will be sent once the position is moved.
                // So let the event pass to children.
                // fix #93, #102
                dispatchTouchEventSupper(e);
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = e.getActionIndex();
                mLastMotionY = e.getY(index);
                mActivePointerId=e.getPointerId(index);
                mNiftyIndicator.onPressDown(e.getX(), e.getY(e.findPointerIndex(mActivePointerId)));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(e);
                if(mActivePointerId != INVALID_POINTER){
                    mLastMotionY = (int) e.getY(e.findPointerIndex(mActivePointerId));
                    mNiftyIndicator.onPressDown(e.getX(),mLastMotionY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                int activePointerIndex = e.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }
                mLastMotionY = e.getY(activePointerIndex);
                mNiftyIndicator.onMove(e.getX(),mLastMotionY);
                float offsetX = mNiftyIndicator.getOffsetX();
                float offsetY = mNiftyIndicator.getOffsetY();

                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mNiftyIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mNiftyIndicator.hasLeftStartPosition();

                // disable move when header not reach top
                if (moveDown && refreshListener != null && !refreshListener.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return dispatchTouchEventSupper(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    movePos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(e);
    }

    /**
     * 功能描述: 防止出现pointerIndex out of range异常<br>
     *
     * @param ev
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }

    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mNiftyIndicator.isInStartPosition())) {
            return;
        }

        int to = mNiftyIndicator.getCurrentPosY() + (int) deltaY;

        // over top
        if (mNiftyIndicator.willOverTop(to)) {
            to = NiftyIndicator.POS_START;
        }

        mNiftyIndicator.setCurrentPos(to);
        int change = to - mNiftyIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = mNiftyIndicator.isUnderTouch();

        // once moved, cancel event will be sent to child
        if (isUnderTouch && !mHasSendCancelEvent && mNiftyIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        // leave initiated position or just refresh complete
        if ((mNiftyIndicator.hasJustLeftStartPosition() && mStatus == Nifty_STATUS_INIT) ||
                (mNiftyIndicator.goDownCrossFinishPosition() && mStatus == Nifty_STATUS_COMPLETE && isEnabledNextPtrAtOnce())) {

            mStatus = Nifty_STATUS_PREPARE;
            mNiftyUIHandlerHolder.onUIRefreshPrepare(this);
            
        }

        // back to initiated position
        if (mNiftyIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();

            // recover event to children
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == Nifty_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (isUnderTouch && !isAutoRefresh() && mPullToRefresh
                    && mNiftyIndicator.crossRefreshLineFromTopToBottom()) {
                tryToPerformRefresh();
            }
            // reach header height while auto refresh
            if (performAutoRefreshButLater() && mNiftyIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }
        mHeaderView.offsetTopAndBottom(change);
        if (!isPinContent()) {
            mContent.offsetTopAndBottom(change);
        }
        invalidate();

        if (mNiftyUIHandlerHolder.hasHandler()) {
            mNiftyUIHandlerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mNiftyIndicator);
        }
        onPositionChange(isUnderTouch, mStatus, mNiftyIndicator);
    }
    protected void onPositionChange(boolean isInTouching, byte status, NiftyIndicator mNiftyIndicator) {
    }

    @SuppressWarnings("unused")
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    private void onRelease(boolean stayForLoading) {

        tryToPerformRefresh();

        if (mStatus == Nifty_STATUS_LOADING) {
            // keep header for fresh
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mNiftyIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mNiftyIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == Nifty_STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * please DO REMEMBER resume the hook
     *
     * @param hook
     */

    public void setRefreshCompleteHook(NiftyUIHandlerHook hook) {
        mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                notifyUIRefreshComplete(true);
            }
        });
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mNiftyIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(NiftyIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != Nifty_STATUS_PREPARE) {
            return false;
        }

        //
        if ((mNiftyIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || mNiftyIndicator.isOverOffsetToRefresh()) {
            mStatus = Nifty_STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mNiftyUIHandlerHolder.hasHandler()) {
            mNiftyUIHandlerHolder.onUIRefreshBegin(this);
           
        }
        if (refreshListener != null) {
            refreshListener.onRefreshBegin(this);
        }
    }

    /**
     * If at the top and not in loading, reset
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == Nifty_STATUS_COMPLETE || mStatus == Nifty_STATUS_PREPARE) && mNiftyIndicator.isInStartPosition()) {
            if (mNiftyUIHandlerHolder.hasHandler()) {
                mNiftyUIHandlerHolder.onUIReset(this);

            }
            mStatus = Nifty_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }
    
    public boolean isPinContent() {
        return (mFlag & FLAG_PIN_CONTENT) > 0;
    }

    protected void onPtrScrollAbort() {
        if (mNiftyIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }
    protected void onPtrScrollFinish() {
        if (mNiftyIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }
    public boolean isRefreshing() {
        return mStatus == Nifty_STATUS_LOADING;
    }

    public void refreshComplete() {
        if (mRefreshCompleteHook != null) {
            mRefreshCompleteHook.reset();
        }

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            performRefreshComplete();
        } else {
            postDelayed(mPerformRefreshCompleteDelay, delay);
        }
    }
    /**
     * Do refresh complete work when time elapsed is greater than {@link #mLoadingMinTime}
     */
    private void performRefreshComplete() {
        mStatus = Nifty_STATUS_COMPLETE;
        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }
        notifyUIRefreshComplete(false);
    }

    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * After hook operation is done, {@link #notifyUIRefreshComplete} will be call in resume action to ignore hook.
         */
        if (mNiftyIndicator.hasLeftStartPosition() && !ignoreHook && mRefreshCompleteHook != null) {
            mRefreshCompleteHook.takeOver();
            return;
        }
        if (mNiftyUIHandlerHolder.hasHandler()) {
            mNiftyUIHandlerHolder.onUIRefreshComplete(this);
        }
        mNiftyIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }
    public void autoRefresh() {
        autoRefresh(true, mDurationToCloseHeader);
    }
    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader);
    }

    private void clearFlag() {
        // remove auto fresh flag
        mFlag = mFlag & ~MASK_AUTO_REFRESH;
    }

    public void autoRefresh(boolean atOnce, int duration) {

        if (mStatus != Nifty_STATUS_INIT) {
            return;
        }

        mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE : FLAG_AUTO_REFRESH_BUT_LATER;

        mStatus = Nifty_STATUS_PREPARE;
        if (mNiftyUIHandlerHolder.hasHandler()) {
            mNiftyUIHandlerHolder.onUIRefreshPrepare(this);
        }
        mScrollChecker.tryToScrollTo(mNiftyIndicator.getOffsetToRefresh(), duration);
        if (atOnce) {
            mStatus = Nifty_STATUS_LOADING;
            performRefresh();
        }
    }
    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    private boolean performAutoRefreshButLater() {
        return (mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER;
    }

    public boolean isEnabledNextPtrAtOnce() {
        return (mFlag & FLAG_ENABLE_NEXT_PTR_AT_ONCE) > 0;
    }
    public void setEnabledNextPtrAtOnce(boolean enable) {
        if (enable) {
            mFlag = mFlag | FLAG_ENABLE_NEXT_PTR_AT_ONCE;
        } else {
            mFlag = mFlag & ~FLAG_ENABLE_NEXT_PTR_AT_ONCE;
        }
    }
    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            mFlag = mFlag | FLAG_PIN_CONTENT;
        } else {
            mFlag = mFlag & ~FLAG_PIN_CONTENT;
        }
    }

    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    @Deprecated
    public void setInterceptEventWhileWorking(boolean yes) {
    }

    @SuppressWarnings({"unused"})
    public View getContentView() {
        return mContent;
    }

    public void setOnNiftyRefreshListener(OnNiftyRefreshListener listener) {
        refreshListener = listener;
    }

    public void addNiftyUIHandler(NiftyUIHandler niftyUIHandler) {
        NiftyUIHandlerHolder.addHandler(mNiftyUIHandlerHolder, niftyUIHandler);
    }
    @SuppressWarnings({"unused"})
    public void removeNiftyRefreshListener(NiftyUIHandler handler) {
        mNiftyUIHandlerHolder = NiftyUIHandlerHolder.removeHandler(mNiftyUIHandlerHolder, handler);
    }

    public void setNiftyIndicator(NiftyIndicator slider) {
        if (mNiftyIndicator != null && mNiftyIndicator != slider) {
            slider.convertFrom(mNiftyIndicator);
        }
        mNiftyIndicator = slider;
    }

    @SuppressWarnings({"unused"})
    public float getResistance() {
        return mNiftyIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        mNiftyIndicator.setResistance(resistance);
    }

    @SuppressWarnings({"unused"})
    public float getDurationToClose() {
        return mDurationToClose;
    }

    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    @SuppressWarnings({"unused"})
    public long getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mNiftyIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return mNiftyIndicator.getOffsetToRefresh();
    }

    @SuppressWarnings({"unused"})
    public void setOffsetToRefresh(int offset) {
        mNiftyIndicator.setOffsetToRefresh(offset);
    }

    @SuppressWarnings({"unused"})
    public float getRatioOfHeaderToHeightRefresh() {
        return mNiftyIndicator.getRatioOfHeaderToHeightRefresh();
    }

    @SuppressWarnings({"unused"})
    public int getOffsetToKeepHeaderWhileLoading() {
        return mNiftyIndicator.getOffsetToKeepHeaderWhileLoading();
    }

    @SuppressWarnings({"unused"})
    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mNiftyIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }

    @SuppressWarnings({"unused"})
    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        mPullToRefresh = pullToRefresh;
    }

    @SuppressWarnings({"unused"})
    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mNiftyIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mNiftyIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            removeCallbacks(this);

            mLastFlingY = 0;

            // fix #47: Scroller should be reused, https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/47
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }

    public interface NiftyUIHandler{
        /**
         * When the content view has reached top and refresh has been completed, view will be reset.
         *
         * @param frame
         */
        public void onUIReset(NiftyRefreshLayout frame);

        /**
         * prepare for loading
         *
         * @param frame
         */
        public void onUIRefreshPrepare(NiftyRefreshLayout frame);

        /**
         * perform refreshing UI
         */
        public void onUIRefreshBegin(NiftyRefreshLayout frame);

        /**
         * perform UI after refresh
         */
        public void onUIRefreshComplete(NiftyRefreshLayout frame);

        public void onUIPositionChange(NiftyRefreshLayout frame, boolean isUnderTouch, byte status, NiftyIndicator NiftyIndicator);
    }

    public static abstract class NiftyUIHandlerHook implements Runnable{
        private Runnable mResumeAction;
        private static final byte STATUS_PREPARE = 0;
        private static final byte STATUS_IN_HOOK = 1;
        private static final byte STATUS_RESUMED = 2;
        private byte mStatus = STATUS_PREPARE;
        public void takeOver() {
            takeOver(null);
        }
        public void takeOver(Runnable resumeAction) {
            if (resumeAction != null) {
                mResumeAction = resumeAction;
            }
            switch (mStatus) {
                case STATUS_PREPARE:
                    mStatus = STATUS_IN_HOOK;
                    run();
                    break;
                case STATUS_IN_HOOK:
                    break;
                case STATUS_RESUMED:
                    resume();
                    break;
            }
        }
        public void reset() {
            mStatus = STATUS_PREPARE;
        }

        public void resume() {
            if (mResumeAction != null) {
                mResumeAction.run();
            }
            mStatus = STATUS_RESUMED;
        }
        public void setResumeAction(Runnable runnable) {
            mResumeAction = runnable;
        }
    }

    public boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }
    public boolean canChildScrollDown(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                if (absListView.getChildCount()>0)
                {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                }else
                {
                    return false;
                }
            } else {
                return ViewCompat.canScrollVertically(view, 1) || view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(1);
        }
    }
    public interface OnNiftyRefreshListener{
        boolean checkCanDoRefresh(NiftyRefreshLayout frame, final View content, final View header);
        void onRefreshBegin(NiftyRefreshLayout frame);
    }
}
