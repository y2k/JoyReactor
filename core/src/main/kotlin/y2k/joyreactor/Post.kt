package y2k.joyreactor

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 9/27/15.
 */
class Post : Serializable, Comparable<Post>, DataSet.Dto {

    override var id: Long = 0

    var title: String? = null
    var image: Image? = null
    var userImage: String? = null
    var userName: String? = null
    var created: Date? = null
    var serverId: String? = null
    var commentCount: Int = 0
    var rating: Float = 0.toFloat()

    fun getUserImage(): UserImage {
        return UserImage.fromUrl(userImage)
    }

    override fun compareTo(other: Post): Int {
        return (id - other.id).toInt()
    }
}