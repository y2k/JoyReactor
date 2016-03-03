package y2k.joyreactor.model

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 9/27/15.
 */
class Post(
    val title: String,
    val image: Image?,
    val userImage: String,
    val userName: String,
    val created: Date,
    val serverId: String,
    val commentCount: Int,
    val rating: Float,
    val tags: List<String>) : Serializable, Comparable<Post>, DataSet.Dto {

    override var id: Long = 0

    // TODO:
    fun getUserImage2(): UserImage {
        return UserImage.fromUrl(userImage)
    }

    override fun compareTo(other: Post): Int {
        return (id - other.id).toInt()
    }
}