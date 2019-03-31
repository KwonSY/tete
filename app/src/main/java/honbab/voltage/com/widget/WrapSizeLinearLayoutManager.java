package honbab.voltage.com.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class WrapSizeLinearLayoutManager extends LinearLayoutManager {
    int measuredWidth = 0;
    int measuredHeight = 0;
    int maxWidth = Integer.MAX_VALUE;
    int maxHeight = Integer.MAX_VALUE;
    boolean manuallyMeasured;
    int lastMeasureItemCount;
    Handler relayoutHandler = new Handler();
    Runnable requestRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    void requestPostLayout() {
        relayoutHandler.post(requestRunnable);
    }

    public WrapSizeLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapSizeLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        relayoutHandler.removeCallbacks(requestRunnable);
        int itemCount = getItemCount();
        if (!manuallyMeasured || lastMeasureItemCount != itemCount) {
            int orientation = getOrientation();
            final int widthSize = maxWidth;
            final int heightSize = maxHeight;
            if (itemCount > 0) {
                int i = 0;
                int acumulatedSize = 0;
                int maxSize = orientation == VERTICAL ? heightSize : widthSize;
                while (i < itemCount && acumulatedSize < maxSize) {
                    View viewForPosition = recycler.getViewForPosition(i);
                    if (viewForPosition != null) {
                        int childWidth = 0;
                        int childHeight = 0;
                        measureChildWithMargins(viewForPosition, 0, 0);
                        childWidth = getDecoratedMeasuredWidth(viewForPosition);
                        childHeight = getDecoratedMeasuredHeight(viewForPosition);
                        recycler.recycleView(viewForPosition);
                        acumulatedSize += (orientation == VERTICAL ? childHeight : childWidth);
                    }
                    i++;
                }
                acumulatedSize = Math.min(acumulatedSize, maxSize);
                int width = orientation == VERTICAL ? widthSize : acumulatedSize;
                int height = orientation == HORIZONTAL ? heightSize : acumulatedSize;
                measuredWidth = width;
                measuredHeight = height;
                lastMeasureItemCount = itemCount;
                manuallyMeasured = true;
                if (acumulatedSize < maxSize) {
                    requestPostLayout();
                } else {
                    super.onLayoutChildren(recycler, state);
                }
            }
        } else {
            super.onLayoutChildren(recycler, state);
        }

    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        relayoutHandler.removeCallbacks(requestRunnable);
        int maxW = View.MeasureSpec.getSize(widthSpec);
        int maxH = View.MeasureSpec.getSize(heightSpec);
        boolean changed = maxW != maxWidth || maxH != maxHeight;
        if (changed && maxW != 0 && maxH != 0) {
            maxWidth = maxW;
            maxHeight = maxH;
            manuallyMeasured = false;
        }
        int width = getOrientation() == HORIZONTAL ? measuredWidth : maxWidth;
        int height = getOrientation() == VERTICAL ? measuredHeight : maxHeight;
        setMeasuredDimension(width, height);
    }
}