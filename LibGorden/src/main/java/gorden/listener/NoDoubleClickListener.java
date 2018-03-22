package gorden.listener;

import android.view.View;

/**
 * 防止连续快速两次点击
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 333;
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(view);
        }
    }

    public abstract void onNoDoubleClick(View view);
}
