package y2k.joyreactor.services.repository

import y2k.joyreactor.model.*

/**
 * Created by y2k on 12/22/15.
 */
class DataContext(val factory: IDataContext) {

    val Posts = factory.register(Post::class)
    val Tags = factory.register(Group::class)
    val TagPosts = factory.register(GroupPost::class)
    val Messages = factory.register(Message::class)
    val comments by lazy { factory.register(Comment::class) }
    val attachments by lazy { factory.register(Attachment::class) }
    val similarPosts by lazy { factory.register(SimilarPost::class) }

    fun saveChanges() = factory.saveChanges()

    infix fun String.eq(value: Any) = this to value
}