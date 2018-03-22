package gorden.widget.refresh;

/**
 *
 * Created by gorden on 2016/4/27.
 */
public class NiftyUIHandlerHolder implements NiftyRefreshLayout.NiftyUIHandler{
    private NiftyRefreshLayout.NiftyUIHandler mHandler;
    private NiftyUIHandlerHolder mNext;

    private boolean contains(NiftyRefreshLayout.NiftyUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }
    private NiftyUIHandlerHolder() {

    }
    public boolean hasHandler() {
        return mHandler != null;
    }
    private NiftyRefreshLayout.NiftyUIHandler getHandler() {
        return mHandler;
    }
    public static void addHandler(NiftyUIHandlerHolder head, NiftyRefreshLayout.NiftyUIHandler handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        NiftyUIHandlerHolder current = head;
        for (; ; current = current.mNext) {

            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        NiftyUIHandlerHolder newHolder = new NiftyUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static NiftyUIHandlerHolder create() {
        return new NiftyUIHandlerHolder();
    }

    public static NiftyUIHandlerHolder removeHandler(NiftyUIHandlerHolder head, NiftyRefreshLayout.NiftyUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        NiftyUIHandlerHolder current = head;
        NiftyUIHandlerHolder pre = null;
        do {
            if (current.contains(handler)) {
                if (pre == null) {
                    head = current.mNext;
                    current.mNext = null;
                    current = head;
                } else {
                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }
        } while (current != null);

        if (head == null) {
            head = new NiftyUIHandlerHolder();
        }
        return head;
    }
    @Override
    public void onUIReset(NiftyRefreshLayout frame) {
        NiftyUIHandlerHolder current = this;
        do {
            final NiftyRefreshLayout.NiftyUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshPrepare(NiftyRefreshLayout frame) {
        if (!hasHandler()) {
            return;
        }
        NiftyUIHandlerHolder current = this;
        do {
            final NiftyRefreshLayout.NiftyUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(NiftyRefreshLayout frame) {
        NiftyUIHandlerHolder current = this;
        do {
            final NiftyRefreshLayout.NiftyUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(NiftyRefreshLayout frame) {
        NiftyUIHandlerHolder current = this;
        do {
            final NiftyRefreshLayout.NiftyUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(NiftyRefreshLayout frame, boolean isUnderTouch, byte status, NiftyIndicator ptrIndicator) {
        NiftyUIHandlerHolder current = this;
        do {
            final NiftyRefreshLayout.NiftyUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
