package gorden.widget.refresh.footer;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * 加载更多布局切换接口
 * @author Gorden
 */
public interface ILoadViewMoreFactory {

    ILoadMoreView madeLoadMoreView();

    /**
     * ListView底部加载更多的布局切换
     */
    interface ILoadMoreView {

        /**
         * 初始化
         *
         * @param footViewHolder
         * @param onClickLoadMoreListener 加载更多的点击事件，需要点击调用加载更多的按钮都可以设置这个监听
         */
        void init(FootViewAdder footViewHolder, OnClickListener onClickLoadMoreListener);

        /**
         * 显示普通布局
         */
        void showNormal();

        /**
         * 显示已经加载完成，没有更多数据的布局
         */
        void showNomore();

        /**
         * 显示正在加载中的布局
         */
        void showLoading();

        /**
         * 显示加载失败的布局
         *
         * @param e
         */
        void showFail(Exception e);

    }

    interface FootViewAdder {

        View addFootView(View view);

        View addFootView(int layoutId);

    }

}
