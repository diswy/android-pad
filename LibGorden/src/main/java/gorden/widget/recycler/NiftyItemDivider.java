package gorden.widget.recycler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gorden Recyclerview　简单分割线
 * Created time 2016/8/17
 */
public class NiftyItemDivider extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int dividerHeight = 1;
    private int leftPadding;
    private int rightPadding;

    private int skipTop = 0;//顶部几个item不画分割线

    private int paintColor = Color.LTGRAY;
    private NiftyRecyclerView mRecyclerView;


    public NiftyItemDivider(NiftyRecyclerView recyclerView) {
        init(recyclerView);
    }

    public NiftyItemDivider(NiftyRecyclerView recyclerView, int dividerHeight) {
        this.dividerHeight = dividerHeight;
        init(recyclerView);
    }

    public NiftyItemDivider(NiftyRecyclerView recyclerView, int dividerHeight, int leftPadding, int rightPadding) {
        this.dividerHeight = dividerHeight;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        init(recyclerView);
    }

    public NiftyItemDivider(int leftPadding, int skipTop) {
        this.leftPadding = leftPadding;
        this.skipTop = skipTop;
        init(null);
    }

    public NiftyItemDivider(NiftyRecyclerView recyclerView, int leftPadding, int rightPadding, int dividerHeight, int skipTop, int paintColor) {
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.dividerHeight = dividerHeight;
        this.skipTop = skipTop;
        this.paintColor = paintColor;
        init(recyclerView);
    }

    private void init(NiftyRecyclerView recyclerView) {
        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setStrokeWidth(dividerHeight);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft() + leftPadding;
        final int right = parent.getWidth() - parent.getPaddingRight() - rightPadding;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;

            int position = parent.getChildAdapterPosition(child);
            if(position>=skipTop && position<parent.getAdapter().getItemCount()-((mRecyclerView!=null&&mRecyclerView.hasInitLoadMoreView)?1:0))
                c.drawLine(left, top - dividerHeight / 2, right, top - dividerHeight / 2, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) >= skipTop && parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount()-((mRecyclerView!=null&&mRecyclerView.hasInitLoadMoreView)?1:0)) {
            outRect.set(0, dividerHeight, 0, 0);
        }

    }
}
