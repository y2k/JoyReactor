package y2k.joyreactor.model

/**
 * Created by y2k on 28/07/16.
 */

class PostsWithNext(val posts: List<Post>, val nextPage: String?)

class PostWithAttachments(val post: Post, val attachments: List<Attachment>)

data class ListState(
    val posts: List<Post>,
    val divider: Int?,
    val hasNew: Boolean)