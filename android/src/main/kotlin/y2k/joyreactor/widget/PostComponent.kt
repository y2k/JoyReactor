package y2k.joyreactor.widget

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.R
import y2k.joyreactor.common.setOnClick
import y2k.joyreactor.common.setVisible
import y2k.joyreactor.common.view
import y2k.joyreactor.viewmodel.PostItemViewModel

/**
 * Created by y2k on 10/07/16.
 */
class PostComponent(itemView: View?) :
    RecyclerView.ViewHolder(itemView), Function1<PostItemViewModel?, Unit> {

    val imagePanel by view<FixedAspectPanel>()
    val image by view<WebImageView>()
    val userImage by view<WebImageView>()
    val videoMark by view<ImageView>()
    val commentCount by view<TextView>()
    val like by view<LikeButton>()
    val favorite by view<FavoriteButton>()
    val time by view<TextView>()
    val userName by view<TextView>()
    val title by view<TextView>()

    override operator fun invoke(item: PostItemViewModel?) {
        if (item == null) return

        if (item.post.image == null) {
            imagePanel.visibility = View.GONE
        } else {
            imagePanel.visibility = View.VISIBLE
            imagePanel.aspect = item.post.image!!.getAspect(0.85f)
            image.image = item.post.image
        }

        userImage.image = item.post.getUserImage2().toImage()
        userName.text = item.post.userName

        videoMark.setVisible(item.post.image?.isAnimated ?: false)
        commentCount.text = "" + item.post.commentCount
        time.text = Companion.prettyTime.format(item.post.created)
        like.like = item.post.myLike

        favorite.isFavorite += item.post.isFavorite

        title.text = item.post.title
        title.setVisible(!item.post.title.isEmpty())

        setOnClick(R.id.like) { item.changeLike() }
        setOnClick(R.id.card, { item.postClicked() })
        setOnClick(R.id.videoMark) { item.playClicked() }
        setOnClick(R.id.favorite) { item.toggleFavorite() }
    }

    companion object {

        val prettyTime = PrettyTime()
    }
}