package android.marshi.nestedwebview;

import android.content.Context;
import android.support.annotation.Size;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 *
 */
public class NestedWebView extends WebView implements NestedScrollingChild {

    private NestedScrollingChildHelper childHelper;

    private int lastTouchY;
    private int touchSlop;
    private boolean isBeingDragged;

    private int[] scrollConsumed = new int[2];
    private int[] scrollOffset = new int[2];

    private boolean isCollapsed;

    private int scrollingMode = SCROLLING_NOTHING_MODE;

    private final static short SCROLLING_NOTHING_MODE = 0;
    private final static short SCROLLING_APPBAR_MODE  = 1;
    private final static short SCROLLING_WEBVIEW_MODE = 2;

    public NestedWebView(Context context) {
        super(context);
        init();
    }

    public NestedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchY = (int) ev.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                isBeingDragged = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int yDiff = Math.abs(y - lastTouchY);
                if (yDiff > touchSlop) {
                    lastTouchY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                lastTouchY = (int) ev.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int yDiff = lastTouchY - y;
                if (dispatchNestedPreScroll(0, yDiff, scrollConsumed, scrollOffset)) {
                    yDiff -= scrollConsumed[1];
                }
                if (isBeingDragged) {
                    lastTouchY = y - scrollOffset[1];
                    if (scrollingMode == SCROLLING_WEBVIEW_MODE) {
                        yDiff = 0;
                    }
                    if (dispatchNestedScroll(0, 0, 0, yDiff, scrollOffset)) {
                        lastTouchY -= scrollOffset[1];
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                stopNestedScroll();
                break;
        }
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (this.isCollapsed) {
                return super.onTouchEvent(ev);
            } else {
                return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.touchSlop = viewConfiguration.getScaledTouchSlop();
        this.childHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    void onChangeCollapseToolbar(boolean b, int dy) {
        this.isCollapsed = b;
        boolean W = getScrollY() == 0;
        boolean S = 0 < dy;
        if (W && !(b && S)) {
            scrollingMode = SCROLLING_APPBAR_MODE;
        } else if (b && (S && W || !S && !W)) {
            scrollingMode = SCROLLING_WEBVIEW_MODE;
        } else {
            scrollingMode = SCROLLING_NOTHING_MODE;
        }
    }

    //NestedScrollingChild
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent(){
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
            int dyUnconsumed, @Size(value = 2) int[] offsetInWindow){
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy,
            @Size(value = 2) int[] consumed,
            @Size(value = 2) int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

}
