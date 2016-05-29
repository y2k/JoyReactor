package y2k.joyreactor

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ListViewHolder
import y2k.joyreactor.common.find
import y2k.joyreactor.common.setVisible
import y2k.joyreactor.model.Post
import y2k.joyreactor.widget.FavoriteButton
import y2k.joyreactor.widget.FixedAspectPanel
import y2k.joyreactor.widget.LikeButton
import y2k.joyreactor.widget.WebImageView

class PostViewHolder(view: View) : ListViewHolder<Post?>(view) {

    val imagePanel = itemView.find<FixedAspectPanel>(R.id.imagePanel)
    val image = itemView.find<WebImageView>(R.id.image)
    val userImage = itemView.find<WebImageView>(R.id.userImage)
    val videoMark = itemView.find<ImageView>(R.id.videoMark)
    val commentCount = itemView.find<TextView>(R.id.commentCount)
    val time = itemView.find<TextView>(R.id.time)
    val userName = itemView.find<TextView>(R.id.userName)
    val likeButton = itemView.find<LikeButton>(R.id.like)
    val favorite = itemView.find<FavoriteButton>(R.id.favorite)

    val prettyTime = PrettyTime()

    override fun update(item: Post?) {
        if (item == null) return
        if (item.image == null) {
            imagePanel.visibility = View.GONE
        } else {
            imagePanel.visibility = View.VISIBLE
            imagePanel.aspect = item.image!!.getAspect(0.7f)
            image.image = item.image
        }

        userImage.image = item.getUserImage2().toImage()
        userName.text = item.userName

        videoMark.setVisible(item.image?.isAnimated ?: false)
        commentCount.text = "" + item.commentCount
        time.text = prettyTime.format(item.created)
        likeButton.like = item.myLike

        favorite.isFavorite += item.isFavorite
    }
}