package y2k.joyreactor

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
class Tag(
    val serverId: String?,
    val title: String,
    var isVisible: Boolean, // TODO: сделать val
    val image: Image?) : Serializable, DataSet.Dto {

    override var id: Long = 0

    val username: String
        get() = serverId!!.substring(MARK_USERNAME.length)

    val isFavorite: Boolean
        get() = serverId != null && serverId.startsWith(MARK_USERNAME)

    companion object {

        private val MARK_USERNAME = "username:"

        fun makeFavorite(username: String): Tag {
            return Tag(MARK_USERNAME + username, username, false, null)
        }

        fun makeFeatured(): Tag {
            return Tag(null, "", false, null)
        }
    }
}