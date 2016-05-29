package y2k.joyreactor.common

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by y2k on 5/29/16.
 */

fun RecyclerView.ViewHolder.setOnClick(id: Int, f: (Int) -> Unit) {
    itemView.find<View>(id).setOnClickListener { f(layoutPosition) }
}