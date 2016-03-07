package y2k.joyreactor

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ComplexViewHolder
import y2k.joyreactor.common.find
import y2k.joyreactor.model.Post
import y2k.joyreactor.presenters.PostListPresenter
import java.util.*

/**
 * Created by y2k on 1/31/16.
 */
class PostAdapter(private val presenter: PostListPresenter) : RecyclerView.Adapter<ComplexViewHolder>() {

    private val prettyTime = PrettyTime()
    private val posts = ArrayList<Post?>()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return (posts[position]?.id ?: 0).toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts[position] == null) 1 else 0
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
        divider?.let { this.posts.add(it, null) }
        notifyDataSetChanged()
    }

    inner class PostViewHolder(parent: ViewGroup) :
        ComplexViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)) {

        val imagePanel = itemView.find<FixedAspectPanel>(R.id.imagePanel)
        val image = itemView.find<WebImageView>(R.id.image)
        val userImage = itemView.find<WebImageView>(R.id.userImage)
        val videoMark = itemView.find<View>(R.id.videoMark)
        val commentCount = itemView.find<TextView>(R.id.commentCount)
        val time = itemView.find<TextView>(R.id.time)
        val userName = itemView.find<TextView>(R.id.userName)

        init {
            itemView.findViewById(R.id.card).setOnClickListener { presenter.postClicked(posts[layoutPosition]!!) }
            itemView.findViewById(R.id.videoMark).setOnClickListener { presenter.playClicked(posts[layoutPosition]!!) }
        }

        override fun bind() {
            val i = posts[adapterPosition]!!

            if (i.image == null) {
                imagePanel.visibility = View.GONE
            } else {
                imagePanel.visibility = View.VISIBLE
                imagePanel.aspect = i.image!!.getAspect(0.5f)
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
            itemView.findViewById(R.id.dividerButton).setOnClickListener { presenter.loadMore() }
        }
    }
}