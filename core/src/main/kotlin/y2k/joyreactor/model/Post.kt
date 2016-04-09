package y2k.joyreactor.model

import y2k.joyreactor.services.repository.arraylist.ArrayListDataSet
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 9/27/15.
 */
data class Post(
    val title: String,
    val image: Image?,
    val userImage: String,
    val userName: String,
    val created: Date,
    val commentCount: Int,
    val rating: Float,
    val tags: List<String>,
    override val id: Long) : Serializable, Comparable<Post>, Dto {

    override fun identify(newId: Long): Post {
        throw UnsupportedOperationException()
    }

    // TODO:
    fun getUserImage2(): UserImage {
        return UserImage.fromUrl(userImage)
    }

    override fun compareTo(other: Post): Int {
        return (id - other.id).toInt()
    }
}