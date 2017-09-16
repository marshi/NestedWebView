package android.marshi.nestedwebview;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
@CoordinatorLayout.DefaultBehavior(NestedWevViewAppBarLayout.Behavior.class)
public class NestedWevViewAppBarLayout extends AppBarLayout {

    public NestedWevViewAppBarLayout(Context context) {
        super(context);
    }

    public NestedWevViewAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static class Behavior extends AppBarLayout.Behavior {

        private Toolbar toolbar;

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                View target, int dx, int dy, int[] consumed, int type) {
            if (this.toolbar == null) {
                this.toolbar = findToolbar(child);
                if (this.toolbar == null) {
                    return;
                }
            }
            int[] toolbarLocation = new int[2];
            toolbar.getLocationOnScreen(toolbarLocation);
            int[] targetLocation = new int[2];
            target.getLocationOnScreen(targetLocation);
            int toolbarBottomY = toolbarLocation[1] + toolbar.getHeight();
            int[] appbarLocation = new int[2];
            int nestedScrollViewTopY = targetLocation[1];
            child.getLocationOnScreen(appbarLocation);
            NestedWebView nestedWebView = (NestedWebView) target;
            if (nestedScrollViewTopY <= toolbarBottomY) {
                nestedWebView.onChangeCollapseToolbar(true, dy);
            } else {
                nestedWebView.onChangeCollapseToolbar(false, dy);
            }
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }

        protected Toolbar findToolbar(ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof Toolbar) {
                    return (Toolbar)view;
                }
                if (view instanceof ViewGroup) {
                    return findToolbar((ViewGroup)view);
                }
            }
            return null;
        }
    }

}
