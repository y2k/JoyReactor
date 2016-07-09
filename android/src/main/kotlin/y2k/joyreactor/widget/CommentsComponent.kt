package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import y2k.joyreactor.common.BindableComponent
import y2k.joyreactor.common.listProperty
import y2k.joyreactor.common.replaceViews
import y2k.joyreactor.model.Comment

/**
 * Created by y2k on 10/07/16.
 */
class CommentsComponent(
    context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs), BindableComponent<List<Comment>> {

    override val value = listProperty<Comment> { onCommentChanged(it) }

    init {
        orientation = VERTICAL
    }

    private fun onCommentChanged(comments: List<Comment>) {
        comments
            .map { CommentComponent(context).apply { value += it } }
            .let { replaceViews(it) }
    }
}