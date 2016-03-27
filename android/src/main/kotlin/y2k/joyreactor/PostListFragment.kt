package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
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
            spinner(R.id.tabs, vm.tagMode)
            visibility(R.id.apply, vm.hasNewPosts)
            command(R.id.apply, { vm.applyNew() })
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
                            itemView.setOnClickListener(R.id.card, { vm.postClicked(layoutPosition) })
                            itemView.setOnClickListener(R.id.videoMark) { vm.playClicked(layoutPosition) }
                        }
                        else -> DividerHolder(parent.inflate(R.layout.item_post_divider)).apply {
                            itemView.setOnClickListener(R.id.dividerButton) { vm.loadMore() }
                        }
                    }
                }
            }
        }
        return view
    }

    class PostViewHolder(view: View) : ListViewHolder<Post?>(view) {

        val imagePanel = itemView.find<FixedAspectPanel>(R.id.imagePanel)
        val image = itemView.find<WebImageView>(R.id.image)
        val userImage = itemView.find<WebImageView>(R.id.userImage)
        val videoMark = itemView.find<View>(R.id.videoMark)
        val commentCount = itemView.find<TextView>(R.id.commentCount)
        val time = itemView.find<TextView>(R.id.time)
        val userName = itemView.find<TextView>(R.id.userName)

        val prettyTime = PrettyTime()

        override fun update(item: Post?) {
            if (item == null) return
            if (item.image == null) {
                imagePanel.visibility = View.GONE
            } else {
                imagePanel.visibility = View.VISIBLE
                imagePanel.aspect = item.image!!.getAspect(0.5f)
                image.image = item.image
            }

            userImage.image = item.getUserImage2().toImage()
            userName.text = item.userName
            videoMark.visibility = if (item.image?.isAnimated ?: false) View.VISIBLE else View.GONE

            commentCount.text = "" + item.commentCount
            time.text = prettyTime.format(item.created)
        }
    }

    class DividerHolder(view: View) : ListViewHolder<Post?>(view) {

        init {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }

        override fun update(item: Post?) {
        }
    }
}