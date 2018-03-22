package gorden.widget.refresh.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import gorden.widget.refresh.LoadingView;
import gorden.widget.refresh.NiftyIndicator;
import gorden.widget.refresh.NiftyRefreshLayout;


/**
 * Created by gorden on 2016/5/14.
 */
public class DefaultHeader extends LinearLayout implements NiftyRefreshLayout.NiftyUIHandler{
    private LoadingView loadingView;
    private TextView textTitle;
    public DefaultHeader(Context context) {
        super(context);
        initView();
    }

    public DefaultHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DefaultHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        loadingView=new LoadingView(getContext());
        addView(loadingView);
        loadingView.getLayoutParams().width=60;
        loadingView.getLayoutParams().height=60;
        loadingView.setDotRadius(5);
        textTitle=new TextView(getContext());
        textTitle.setText("玩命加载中...");
        textTitle.setTextSize(13);
        addView(textTitle);
        ((LayoutParams)textTitle.getLayoutParams()).leftMargin=50;
    }

    @Override
    public void onUIReset(NiftyRefreshLayout frame) {
        loadingView.reset();
    }

    @Override
    public void onUIRefreshPrepare(NiftyRefreshLayout frame) {
        textTitle.setText("下拉刷新...");
    }

    @Override
    public void onUIRefreshBegin(NiftyRefreshLayout frame) {
        loadingView.start();
        textTitle.setText("玩命加载中...");
    }

    @Override
    public void onUIRefreshComplete(NiftyRefreshLayout frame) {
        loadingView.stop();
        textTitle.setText("加载完成...");
    }

    @Override
    public void onUIPositionChange(NiftyRefreshLayout frame, boolean isUnderTouch, byte status, NiftyIndicator NiftyIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = NiftyIndicator.getCurrentPosY();
        final int lastPos = NiftyIndicator.getLastPosY();
        float currentPercent = Math.min(1f, NiftyIndicator.getCurrentPercent());
        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == NiftyRefreshLayout.Nifty_STATUS_PREPARE) {
                textTitle.setText("下拉刷新...");
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == NiftyRefreshLayout.Nifty_STATUS_PREPARE) {
                textTitle.setText("释放刷新...");
            }
        }
        setProgress(currentPercent);
    }

    private void setProgress(float progress) {
        loadingView.setProgress(progress);
    }
}
