package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Post
import y2k.joyreactor.viewmodel.PostListViewModel

/**
 * Created by y2k on 9/26/15.
 */
class PostListFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)
        view.find<RecyclerView>(R.id.list).apply { addItemDecoration(ItemDividerDecoration(this)) }
        val vm = ServiceLocator.resolve<PostListViewModel>(lifeCycleService)

        bindingBuilder(view) {
            spinnerTemp(R.id.tabs, vm.quality)
            visibility(R.id.apply, vm.hasNewPosts)
            visibility(R.id.error, vm.isError)
            command(R.id.apply) { vm.applyNew() }
            refreshLayout(R.id.refresher) {
                isRefreshing(vm.isBusy)
                command { vm.reloadFirstPage() }
            }
            recyclerView(R.id.list, vm.posts) {
                itemId { it?.id ?: 0L }
                itemViewType { if (it.value == null) 2 else 1 }
                viewHolderWithType { parent, type ->
                    when (type) {
                        1 -> PostViewHolder(parent.inflate(R.layout.item_feed)).apply {
                            setOnClick(R.id.like) { vm.changeLike(it) }
                            setOnClick(R.id.card, { vm.postClicked(it) })
                            setOnClick(R.id.videoMark) { vm.playClicked(it) }
                            setOnClick(R.id.favorite) { vm.toggleFavorite(it) }
                        }
                        else -> DividerHolder(parent.inflate(R.layout.item_post_divider)).apply {
                            setOnClick(R.id.dividerButton) { vm.loadMore() }
                        }
                    }
                }
            }
        }
        return view
    }

    class DividerHolder(view: View) : ListViewHolder<Post?>(view) {

        init {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }

        override fun update(item: Post?) {
        }
    }
}