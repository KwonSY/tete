package honbab.voltage.com.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int mSpaceTop;
    private int mSpaceRight;
    private int mSpaceBottom;
    private int mSpaceLeft;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    public SpacesItemDecoration(int top, int right, int bottom, int left) {
        this.mSpaceTop = top;
        this.mSpaceRight = right;
        this.mSpaceBottom = bottom;
        this.mSpaceLeft = left;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
//        Log.e("abc", "space -= " + space);
//        Log.e("abc", "outRect.left = " + outRect.left);
//        Log.e("abc", "outRect.right = " + outRect.right);
        outRect.left = space;
        outRect.right = space;
//        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = space;
            outRect.top = 0;
        } else {
            outRect.top = 0;
        }
    }
}