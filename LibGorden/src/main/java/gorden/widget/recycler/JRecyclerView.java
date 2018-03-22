package gorden.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * document
 * Created by Gordn on 2017/7/4.
 */

public class JRecyclerView extends RecyclerView {
    private static final int ITEM_TYPE_LOADMORE = Integer.MIN_VALUE;
    private static final int ITEM_TYPE_FOOTER_INIT = 10000;
    private static final int ITEM_TYPE_HEADER_INIT = 20000;

    private boolean hasMore = false;
    private boolean showMore = false;
    private boolean loadMoreEnable = true;
    private boolean Loading = false;

    private WrapAdapter mWrapAdapter;
    private JLoadMoreView mLoadMoreView;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    private JLoadMoreListener mLoadMoreListener;

    public JRecyclerView(Context context) {
        this(context, null);
    }

    public JRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(new JDataObserver());
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + ITEM_TYPE_HEADER_INIT, view);
        if (mWrapAdapter != null) mWrapAdapter.notifyDataSetChanged();
    }

    public void addFooterView(View view) {
        mFooterViews.put(mFooterViews.size() + ITEM_TYPE_FOOTER_INIT, view);
        if (mWrapAdapter != null) mWrapAdapter.notifyDataSetChanged();
    }

    public void setLoadMoreView(JLoadMoreView loadMoreView) {
        if (!(loadMoreView instanceof View)) {
            throw new IllegalStateException("JLoadMoreView must is a View?");
        }

        mLoadMoreView = loadMoreView;

        if (mWrapAdapter != null) mWrapAdapter.notifyDataSetChanged();

        removeOnScrollListener(defaultScrollListener);
        if (!mLoadMoreView.shouldLoadMore(this)) {
            addOnScrollListener(defaultScrollListener);
        }
    }


    public void startLoadMore() {
        if (!Loading && loadMoreEnable && hasMore) {
            Loading = true;
            if (mLoadMoreView != null)
                mLoadMoreView.onLoadMore(this);
            if (mLoadMoreListener != null)
                mLoadMoreListener.onLoadMore(this);
        }
    }

    public void loadMoreComplete(boolean hasMore) {
        Loading = false;
        this.hasMore = hasMore;
        if (mLoadMoreView != null)
            mLoadMoreView.onComplete(this, hasMore);
    }

    public void loadMoreError(int errorCode){
        Loading = false;
        if (mLoadMoreView != null)
            mLoadMoreView.onError(this,errorCode);
    }

    private OnScrollListener defaultScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {
                startLoadMore();
            }
        }
    };

    public interface JLoadMoreListener {
        void onLoadMore(JRecyclerView recyclerView);
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    public void setLoadMoreListener(JLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    @SuppressWarnings("unchecked")
    private class WrapAdapter extends Adapter {
        private Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mHeaderViews.get(viewType);

            if (itemView == null && viewType == ITEM_TYPE_LOADMORE) {
                itemView = (View) mLoadMoreView;
            }

            if (itemView == null) itemView = mFooterViews.get(viewType);

            if (itemView != null) {
                return new SimpleViewHolder(itemView);
            } else {
                return adapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isContent(position)) {
                adapter.onBindViewHolder(holder, position - mHeaderViews.size());
            }
        }

        @Override
        public int getItemCount() {
            int count = adapter.getItemCount();

            if (count > 0) {
                return count + mHeaderViews.size() + mFooterViews.size() + (mLoadMoreView == null||!showMore ? 0 : 1);
            }
            return mHeaderViews.size() + mFooterViews.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (position < mHeaderViews.size()) {
                return mHeaderViews.keyAt(position);
            }

            if (showMore&&mLoadMoreView != null && position == getItemCount() - 1) {
                return ITEM_TYPE_LOADMORE;
            }

            if (position >= mHeaderViews.size() + adapter.getItemCount()) {
                return mFooterViews.keyAt(position - mHeaderViews.size() - adapter.getItemCount());
            }
            return adapter.getItemViewType(position - mHeaderViews.size());
        }

        private boolean isContent(int position) {
            return position >= mHeaderViews.size() && !(showMore&&mLoadMoreView != null && position == getItemCount() - 1)
                    && position < mHeaderViews.size() + adapter.getItemCount();
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            private SimpleViewHolder(View itemView) {
                super(itemView);
                itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (!isContent(position))
                            return gridManager.getSpanCount();
                        return 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            adapter.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            int position = holder.getLayoutPosition();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && !isContent(position)) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
        }
    }

    private class JDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mWrapAdapter != null)
                mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mWrapAdapter != null) mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mWrapAdapter != null)
                mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mWrapAdapter != null) mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mWrapAdapter != null) mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }
}
