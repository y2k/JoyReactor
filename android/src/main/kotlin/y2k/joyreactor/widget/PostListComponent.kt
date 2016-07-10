package y2k.joyreactor.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.R
import y2k.joyreactor.common.*
import y2k.joyreactor.viewmodel.PostItemViewModel
import java.util.*

/**
 * Created by y2k on 10/07/16.
 */
class PostListComponent(
    context: Context?, attrs: AttributeSet?) :
    RecyclerView(context, attrs), BindableComponent<ListWithDivider<PostItemViewModel>?> {

    override val value = property<ListWithDivider<PostItemViewModel>>()

    var commandLoadMore: () -> Unit = {}

    private val items = ArrayList<PostItemViewModel?>()
    private val adapter = Adapter()

    init {
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        setAdapter(adapter)

        value.subscribe {
            items.clear()
            if (it != null) {
                items.addAll(it.items)
                it.divider?.let { items.add(it, null) }
            }
            adapter.notifyDataSetChanged()
        }
    }

    inner class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            setHasStableIds(true)
        }

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder as Function1<PostItemViewModel?, Unit>)(items[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                2 -> DividerHolder(parent.inflate(R.layout.item_post_divider)).apply {
                    setOnClick(R.id.dividerButton) { commandLoadMore() }
                }
                else -> PostComponent(parent.inflate(R.layout.item_feed))
            }
        }

        override fun getItemViewType(position: Int): Int = if (items[position] == null) 2 else 1
        override fun getItemId(position: Int): Long = items[position]?.post?.id ?: 0L
        override fun getItemCount(): Int = items.size
    }

    private class DividerHolder(view: View) : ViewHolder(view), Function1<PostItemViewModel?, Unit> {

        init {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }

        override fun invoke(item: PostItemViewModel?) {
        }
    }
}