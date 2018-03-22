package gorden.widget.selector;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import gorden.lib.R;

/**
 * SelectorCheckBox
 * Created by gorden on 2016/8/5.
 */
public class SelectorCheckBox extends SelectorText implements Checkable {

    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};
    boolean isChecked = false;
    boolean checkable = false;

    public SelectorCheckBox(Context context) {
        this(context,null);
    }

    public SelectorCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectorCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorText, defStyleAttr,0);
        checkable = a.getBoolean(R.styleable.SelectorText_s_checkable,false);
        setCheckable(checkable);

        a.recycle();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked)
        {
            isChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
        if(checkable) setClickable(true);
    }

    @Override
    public boolean performClick() {
        if(checkable) toggle();
        return super.performClick();
    }
}
