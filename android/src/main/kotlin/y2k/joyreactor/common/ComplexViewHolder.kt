package y2k.joyreactor.common

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by y2k on 11/30/15.
 */
abstract class ComplexViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind() {
        // stub
    }
}