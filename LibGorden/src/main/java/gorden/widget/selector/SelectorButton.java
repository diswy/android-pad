package gorden.widget.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * SelectorButton
 * Created by gorden on 2016/8/4.
 */
public class SelectorButton extends SelectorText{
    public SelectorButton(Context context) {
        this(context,null);
    }

    public SelectorButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public SelectorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setGravity(Gravity.CENTER);
    }
}
