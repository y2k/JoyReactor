package y2k.joyreactor

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ComplexViewHolder
import y2k.joyreactor.presenters.PostListPresenter
import java.util.*

/**
 * Created by y2k on 1/31/16.
 */
class PostAdapter(private val presenter: PostListPresenter) : RecyclerView.Adapter<ComplexViewHolder>() {

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

    inner class PostViewHolder(parent: ViewGroup) :
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

            itemView.findViewById(R.id.card).setOnClickListener { v -> presenter.postClicked(posts[adapterPosition]) }
            itemView.findViewById(R.id.videoMark).setOnClickListener { v -> presenter.playClicked(posts[adapterPosition]) }
        }

        override fun bind() {
            val i = posts[adapterPosition]

            if (i.image == null) {
                imagePanel.visibility = View.GONE
            } else {
                imagePanel.visibility = View.VISIBLE
                imagePanel.setAspect(i.image!!.getAspect(0.5f))
                image.setImage(i.image)
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

    companion object {

        private val DIVIDER = Post()
    }
}