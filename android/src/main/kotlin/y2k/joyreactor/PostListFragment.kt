package y2k.joyreactor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ComplexViewHolder
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.ItemDividerDecoration
import y2k.joyreactor.common.Optional
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.presenters.PostListPresenter
import java.util.*

/**
 * Created by y2k on 9/26/15.
 */
class PostListFragment : Fragment() {

    private var presenter: PostListPresenter? = null
    private val adapter = PostAdapter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_posts, container, false)

        view.findViewById(R.id.error).visibility = View.GONE

        val list = view.findViewById(R.id.list) as RecyclerView
        list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        list.adapter = adapter
        list.addItemDecoration(ItemDividerDecoration(list))

        presenter = ServiceLocator.getInstance().providePostListPresenter(
                object : PostListPresenter.View {

                    override fun setBusy(isBusy: Boolean) {
                        // TODO:
                    }

                    override fun reloadPosts(posts: List<Post>, divider: Int?) {
                        adapter.reloadData(posts, divider)
                    }

                    override fun setHasNewPosts(hasNewPosts: Boolean) {
                        (getView()!!.findViewById(R.id.apply) as ReloadButton).setVisibility(hasNewPosts)
                    }
                })

        view.findViewById(R.id.apply).setOnClickListener { v -> presenter?.applyNew() }
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter?.activate()
    }

    override fun onPause() {
        super.onPause()
        presenter?.deactivate()
    }

    internal inner class PostAdapter : RecyclerView.Adapter<ComplexViewHolder>() {

        private val prettyTime = PrettyTime()
        private var posts: ArrayList<Post> = ArrayList()

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return posts[position].id.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return if (posts[position] === DIVIDER) 1 else 0
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): ComplexViewHolder {
            return if (itemType == 0) PostViewHolder(viewGroup) else DividerHolder(viewGroup)
        }

        override fun onBindViewHolder(h: ComplexViewHolder, position: Int) {
            h.bind()
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        fun reloadData(posts: List<Post>, divider: Int?) {
            this.posts.clear()
            this.posts.addAll(posts)
            divider?.let { this.posts.add(it, DIVIDER) }
            notifyDataSetChanged()
        }

        internal inner class PostViewHolder(parent: ViewGroup) :
                ComplexViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)) {

            val imagePanel: FixedAspectPanel
            val image: WebImageView
            val userImage: WebImageView
            val videoMark: View
            val commentCount: TextView
            val time: TextView
            val userName: TextView

            init {
                image = itemView.findViewById(R.id.image) as WebImageView
                imagePanel = itemView.findViewById(R.id.imagePanel) as FixedAspectPanel
                userImage = itemView.findViewById(R.id.userImage) as WebImageView
                videoMark = itemView.findViewById(R.id.videoMark)
                commentCount = itemView.findViewById(R.id.commentCount) as TextView
                time = itemView.findViewById(R.id.time) as TextView
                userName = itemView.findViewById(R.id.userName) as TextView

                itemView.findViewById(R.id.card).setOnClickListener { v -> presenter?.postClicked(posts[adapterPosition]) }
                itemView.findViewById(R.id.videoMark).setOnClickListener { v -> presenter?.playClicked(posts[adapterPosition]) }
            }

            override fun bind() {
                val i = posts[adapterPosition]

                if (i.image == null) {
                    imagePanel.visibility = View.GONE
                } else {
                    imagePanel.visibility = View.VISIBLE
                    imagePanel.setAspect(i.image!!.getAspect(0.5f))

                    val height = (200 / i.image!!.getAspect(0.5f)).toInt()
                    ImageRequest()
                            .setUrl(i.image)
                            .setSize(200, height)
                            .to(i.image, { image.setImageBitmap(it) })
                }

                userImage.setImage(i.getUserImage2().toImage())
                userName.text = i.userName
                videoMark.visibility = if (i.image?.isAnimated ?: false) View.VISIBLE else View.GONE

                commentCount.text = "" + i.commentCount
                time.text = prettyTime.format(i.created)
            }
        }

        internal inner class DividerHolder(parent: ViewGroup) :
                ComplexViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_divider, parent, false)) {

            init {
                (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                itemView.findViewById(R.id.dividerButton).setOnClickListener { v -> presenter!!.loadMore() }
            }
        }
    }

    companion object {

        private val DIVIDER = Post()
    }
}