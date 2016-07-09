package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import y2k.joyreactor.R
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Comment

/**
* Created by y2k on 10/07/16.
*/
class CommentComponent(
    context: Context?, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), BindableComponent<Comment> {

    override val value = property(Comment())

    private val rating by view<TextView>()
    private val text by view<TextView>()
    private val replies by view<TextView>()
    private val avatar by view<WebImageView>()
    private val attachment by view<WebImageView>()

    init {
        View.inflate(context, R.layout.item_comment, this)
        value.subscribe { onUpdate(it) }
    }

    private fun onUpdate(item: Comment?) {
        if (item == null) return

        updateMargin(left = (28 * item.level + 8).px())

        text.text = item.text
        avatar.image = item.userImageObject.toImage()
        rating.text = "" + item.rating
        replies.text = "" + item.replies

        attachment.setVisible(item.attachment != null)
        attachment.image = item.attachment
    }
}