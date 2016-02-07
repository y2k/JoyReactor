package y2k.joyreactor.common

import android.support.v7.widget.RecyclerView

/**
 * Created by y2k on 2/7/16.
 */
abstract class ListAdapter<T, TVH : RecyclerView.ViewHolder> : RecyclerView.Adapter<TVH>() {

    protected var items = emptyList<T>()

    init {
        setHasStableIds(true)
    }

    fun update(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}