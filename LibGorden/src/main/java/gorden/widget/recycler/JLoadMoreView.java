package gorden.widget.recycler;

/**
 * document
 * Created by Gordn on 2017/7/4.
 */

public interface JLoadMoreView {
    boolean shouldLoadMore(JRecyclerView recyclerView);
    void onLoadMore(JRecyclerView recyclerView);
    void onComplete(JRecyclerView recyclerView,boolean hasMore);
    void onError(JRecyclerView recyclerView,int errorCode);
}
