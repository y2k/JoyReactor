package y2k.joyreactor.model

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
data class Tag(
    val serverId: String?,
    val title: String,
    val isVisible: Boolean,
    val image: Image?,
    override val id: Long = 0) : Serializable, DataSet.Dto {

    override fun identify(newId: Long): Tag {
        return copy(id = newId)
    }

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