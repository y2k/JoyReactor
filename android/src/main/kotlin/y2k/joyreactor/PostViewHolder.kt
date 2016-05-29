package y2k.joyreactor

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ListViewHolder
import y2k.joyreactor.common.setVisible
import y2k.joyreactor.common.view
import y2k.joyreactor.model.Post
import y2k.joyreactor.widget.FavoriteButton
import y2k.joyreactor.widget.FixedAspectPanel
import y2k.joyreactor.widget.LikeButton
import y2k.joyreactor.widget.WebImageView

class PostViewHolder(view: View) : ListViewHolder<Post?>(view) {

    val imagePanel by view<FixedAspectPanel>()
    val image by view<WebImageView>()
    val userImage by view<WebImageView>()
    val videoMark by view<ImageView>()
    val commentCount by view<TextView>()
    val like by view<LikeButton>()
    val favorite by view<FavoriteButton>()
    val time by view<TextView>()
    val userName by view<TextView>()

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
        time.text = Companion.prettyTime.format(item.created)
        like.like = item.myLike

        favorite.isFavorite += item.isFavorite
    }

    companion object {
        val prettyTime = PrettyTime()
    }
}