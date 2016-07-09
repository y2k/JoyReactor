package y2k.joyreactor.common

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by y2k on 11/28/15.
 */
class ItemDividerDecoration(list: RecyclerView) : RecyclerView.ItemDecoration() {

    private val divider = 4.px()

    init {
        list.setPadding(divider, divider, divider, divider)
        list.clipToPadding = false
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.right += divider
        outRect.bottom += divider
        outRect.top += divider
        outRect.left += divider
    }
}