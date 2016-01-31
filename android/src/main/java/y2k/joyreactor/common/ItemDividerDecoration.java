package y2k.joyreactor.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import y2k.joyreactor.App;

/**
 * Created by y2k on 11/28/15.
 */
public class ItemDividerDecoration extends RecyclerView.ItemDecoration {

    private int divider = (int) (4 * App.Companion.getInstance().getResources().getDisplayMetrics().density);

    public ItemDividerDecoration(RecyclerView list) {
        list.setPadding(divider, divider, divider, divider);
        list.setClipToPadding(false);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right += divider;
        outRect.bottom += divider;
        outRect.top += divider;
        outRect.left += divider;
    }
}