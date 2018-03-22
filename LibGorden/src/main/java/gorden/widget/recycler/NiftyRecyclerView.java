package gorden.widget.recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gorden.widget.refresh.NiftyRefreshLayout;
import gorden.widget.refresh.footer.DefaultFooter;
import gorden.widget.refresh.footer.ILoadViewMoreFactory;
import gorden.widget.refresh.footer.RecyclerViewHandler;
import gorden.widget.refresh.header.MaterialHeader;

/**
 * 支持添加头部 刷新操作
 * Created time 2016/8/17
 */
public class NiftyRecyclerView extends NiftyRefreshLayout {
    public RecyclerView mRecyclerView;
    //    private DefaultHeader mDefaultHeader;
    private MaterialHeader materialHeader;
    private boolean refreshEnable = true;

    private LoadingListener mLoadingListener;

    private WrapAdapter mWrapAdapter;
    private static final int HEADER_INIT_INDEX = 10001;
    private static final int FOOTER_INIT_INDEX = 20001;
    private static List<Integer> sHeaderTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化
    private static List<Integer> sFooterTypes = new ArrayList<>();
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();

    public NiftyRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public NiftyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NiftyRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void initView(Context context) {
        mRecyclerView = new RecyclerView(context);
        addView(mRecyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        materialHeader = new MaterialHeader(context);
        materialHeader.setPadding(0, 25, 0, 25);
        materialHeader.setNiftyRefreshLayout(this);
        setHeaderView(materialHeader);
        addNiftyUIHandler(materialHeader);

//        mDefaultHeader = new DefaultHeader(context);
//        mDefaultHeader.setPadding(0, 25, 0, 25);
//        setHeaderView(mDefaultHeader);
//        addNiftyUIHandler(mDefaultHeader);

        /**
         * 绑定刷新事件
         */
        setOnNiftyRefreshListener(new OnNiftyRefreshListener() {
            @Override
            public boolean checkCanDoRefresh(NiftyRefreshLayout frame, View content, View header) {
                return !frame.canChildScrollUp(content) && refreshEnable && mLoadingListener != null && !isLoading;
            }

            @Override
            public void onRefreshBegin(NiftyRefreshLayout frame) {
                mLoadingListener.onRefresh();
            }
        });

    }

    /*======================================添加header and footer==========================================================*/
    public void addHeaderView(View view) {
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
    }

    public void addFooterView(View view) {
        sFooterTypes.add(FOOTER_INIT_INDEX + mFooterViews.size());
        mFooterViews.add(view);
    }

    //根据header的ViewType判断是哪个header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    private View getFooterViewByType(int itemType) {
        if (!isFooterType(itemType)) {
            return null;
        }
        return mFooterViews.get(itemType - FOOTER_INIT_INDEX);
    }

    //判断一个type是否为HeaderType
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    private boolean isFooterType(int itemViewType) {
        return mFooterViews.size() > 0 && sFooterTypes.contains(itemViewType);
    }

    //判断是否是XRecyclerView保留的itemViewType
    private boolean isReservedItemViewType(int itemViewType) {
        if (sHeaderTypes.contains(itemViewType) || sFooterTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }
    /*======================================添加header and footer  end==========================================================*/

    /**
     * 设置recyclerview 适配器
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        mRecyclerView.setAdapter(mWrapAdapter);

        if (!adapter.hasObservers())
            adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * 设置布局管理器
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener scrollListener) {
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * 设置分割线
     *
     * @param itemDercoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDercoration) {
        mRecyclerView.addItemDecoration(itemDercoration);
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    ;

    public class WrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public boolean isHeader(int position) {
            return position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            if (mFooterViews.size() > 0) {
                return position >= getItemCount() - mFooterViews.size();
            } else
                return false;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return mFooterViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (isFooterType(viewType)) {
                return new SimpleViewHolder(getFooterViewByType(viewType));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position)) return;
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - getHeadersCount();
            if (isReservedItemViewType(adapter.getItemViewType(adjPosition))) {
                throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
            }

            if (isHeader(position)) {
                return sHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                position = getFootersCount() - (getItemCount() - position);
                return sFooterTypes.get(position);
            }

            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - (getHeadersCount());
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    protected boolean isLoading = false;
    protected boolean isAutoLoadMore = true;
    protected boolean isLoadMoreEnable = false;
    protected boolean hasInitLoadMoreView = false;

    protected ILoadViewMoreFactory loadViewFactory = new DefaultFooter(getContext());
    protected ILoadViewMoreFactory.ILoadMoreView mLoadMoreView;

    protected RecyclerViewHandler recyclerViewHandler = new RecyclerViewHandler();

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        if (isLoadMoreEnable == loadMoreEnable) {
            return;
        }
        this.isLoadMoreEnable = loadMoreEnable;

        if (!hasInitLoadMoreView && isLoadMoreEnable && mLoadingListener != null) {
            mLoadMoreView = loadViewFactory.madeLoadMoreView();
            hasInitLoadMoreView = recyclerViewHandler.handleSetAdapter(this, mLoadMoreView,
                    onClickLoadMoreListener);
            recyclerViewHandler.setOnScrollBottomListener(mRecyclerView, onScrollBottomListener);
        }
    }

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    public interface OnScrollBottomListener {
        void onScorllBootom();
    }

    public interface ViewHandler {
        /**
         * @param contentView
         * @param loadMoreView
         * @param onClickLoadMoreListener
         * @return 是否有 init ILoadMoreView
         */
        boolean handleSetAdapter(View contentView, ILoadViewMoreFactory.ILoadMoreView loadMoreView, View.OnClickListener onClickLoadMoreListener);

        void setOnScrollBottomListener(View contentView, OnScrollBottomListener onScrollBottomListener);

    }

    protected OnScrollBottomListener onScrollBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScorllBootom() {
            if (isAutoLoadMore && isLoadMoreEnable && !isLoading() && !isRefreshing()) {
                // 此处可加入网络是否可用的判断
                loadMore();
            }
        }
    };

    protected View.OnClickListener onClickLoadMoreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            loadMore();
        }
    };

    void loadMore() {
        isLoading = true;
        mLoadMoreView.showLoading();
        if (mLoadingListener != null) mLoadingListener.onLoadMore();
    }

    public void loadMoreComplete(boolean hasMore) {
        isLoading = false;
        if (hasMore) {
            setLoadMoreEnable(true);
            mLoadMoreView.showNormal();
        } else {
            setLoadMoreEnable(false);
            setNoMoreData();
        }
    }

    public void loadMoreFail(Exception e) {
        isLoading = false;
        if (mLoadMoreView != null) mLoadMoreView.showFail(e);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setNoMoreData() {
        isLoadMoreEnable = false;
        if (mLoadMoreView != null) mLoadMoreView.showNomore();
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }
}
