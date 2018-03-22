package gorden.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * 分组adapter
 * Created by Gordn on 2017/3/10.
 */

public abstract class SectionedAdapter<H extends RecyclerView.ViewHolder,
        VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected static final int TYPE_SECTION_HEADER = -1;
    protected static final int TYPE_ITEM = -2;

    public int[] sectionForPosition = null;
    public int[] positionWithinSection = null;

    private boolean[] isHeader = null;

    private int count = 0;

    private SparseArray<Integer> mSections = new SparseArray<Integer>();

    public SectionedAdapter() {
        registerAdapterDataObserver(new SectionDataObserver());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setupIndices();
    }

    @Override
    public int getItemCount() {
        return count;
    }

    private void setupIndices() {
        count = countItems();
        allocateAuxiliaryArrays(count);
        precomputeIndices();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (isSectionHeaderViewType(viewType)) {
            viewHolder = onCreateSectionHeaderViewHolder(parent, viewType);
        } else {
            viewHolder = onCreateItemViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int section = sectionForPosition[position];
        int index = positionWithinSection[position];

        if (isSectionHeaderPosition(position)) {
            onBindSectionHeaderViewHolder((H) holder, section);
        } else {
            onBindItemViewHolder((VH) holder, section, index);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (sectionForPosition == null) {
            setupIndices();
        }

        int section = sectionForPosition[position];
        int index = positionWithinSection[position];

        if (isSectionHeaderPosition(position)) {
            return getSectionHeaderViewType(section);
        } else {
            return getSectionItemViewType(section, index);
        }
    }

    private int countItems() {
        int count = 0;
        int sections = getSectionCount();

        for (int i = 0; i < sections; i++) {
            count += 1 + getItemCountForSection(i);
        }
        return count;
    }

    private void precomputeIndices() {
        int sections = getSectionCount();
        int index = 0;
        int position = 0;

        for (int i = 0; i < sections; i++) {
            setPrecomputedItem(index, true, i, 0);
            mSections.append(index, -1);
            index++;

            for (int j = 0; j < getItemCountForSection(i); j++) {
                setPrecomputedItem(index, false, i, j);
                mSections.append(index, position);
                index++;
                position++;
            }
        }
    }


    private void allocateAuxiliaryArrays(int count) {
        sectionForPosition = new int[count];
        positionWithinSection = new int[count];
        isHeader = new boolean[count];
    }

    private void setPrecomputedItem(int index, boolean isHeader, int section, int position) {
        this.isHeader[index] = isHeader;
        sectionForPosition[index] = section;
        positionWithinSection[index] = position;
    }

    protected int getSectionHeaderViewType(int section) {
        return TYPE_SECTION_HEADER;
    }

    protected int getSectionItemViewType(int section, int position) {
        return TYPE_ITEM;
    }

    /**
     * Returns true if the argument position corresponds to a header
     */
    public boolean isSectionHeaderPosition(int position) {
        if (isHeader == null) {
            setupIndices();
        }
        return isHeader[position];
    }

    protected boolean isSectionHeaderViewType(int viewType) {
        return viewType == TYPE_SECTION_HEADER;
    }

    protected abstract int getSectionCount();

    protected abstract int getItemCountForSection(int section);

    protected int getItemPosition(int layoutPosition) {
        return mSections.get(layoutPosition);
    }

    /**
     * Creates a ViewHolder of class H for a Header
     */
    protected abstract H onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType);

    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindSectionHeaderViewHolder(H holder, int section);

    protected abstract void onBindItemViewHolder(VH holder, int section, int position);

    class SectionDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            setupIndices();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            setupIndices();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            setupIndices();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            setupIndices();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            setupIndices();
        }
    }
}
