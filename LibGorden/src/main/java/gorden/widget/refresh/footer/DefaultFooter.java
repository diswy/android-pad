package gorden.widget.refresh.footer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import gorden.widget.refresh.LoadingView;


/**
 * 默认上啦加载
 * Created by gorden on 2016/5/16.
 */
public class DefaultFooter extends LinearLayout implements ILoadViewMoreFactory {
    private LoadingView loadingView;
    private TextView textTitle;

    public DefaultFooter(Context context) {
        super(context);
        initView();
    }

    public DefaultFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DefaultFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        loadingView = new LoadingView(getContext());
        addView(loadingView);
        loadingView.getLayoutParams().width = 60;
        loadingView.getLayoutParams().height = 60;
        loadingView.setDotRadius(5);
        textTitle = new TextView(getContext());
        textTitle.setTextSize(13);
        addView(textTitle);
        ((LayoutParams) textTitle.getLayoutParams()).leftMargin = 50;
        setPadding(0, 10, 0, 10);
    }

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    private class LoadMoreHelper implements ILoadMoreView {

        protected OnClickListener onClickRefreshListener;

        @Override
        public void init(FootViewAdder footViewHolder, OnClickListener onClickRefreshListener) {
            footViewHolder.addFootView(DefaultFooter.this);
            this.onClickRefreshListener = onClickRefreshListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            setVisibility(VISIBLE);
            if (getLayoutParams() != null)
                getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
            textTitle.setText("上拉加载更多");
            loadingView.stop();
            loadingView.reset();
            textTitle.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showLoading() {
            setVisibility(VISIBLE);
            if (getLayoutParams() != null)
                getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
            textTitle.setText("正在加载中...");
            loadingView.start();
            textTitle.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception exception) {
            setVisibility(VISIBLE);
            if (getLayoutParams() != null)
                getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
            textTitle.setText("加载失败，点击重新加载");
            loadingView.stop();
            textTitle.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showNomore() {
            textTitle.setText("全部加载完成");
            loadingView.stop();
            setVisibility(GONE);
            textTitle.setOnClickListener(null);
            if (getLayoutParams() != null) getLayoutParams().height = 0;
        }

    }
}
