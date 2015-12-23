package y2k.joyreactor

import y2k.joyreactor.services.repository.DataContext
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
open class Tag : Serializable, DataContext.Dto {

    override var id: Long = 0

    var isMine: Boolean = false

    var title: String? = null
    var image: Image? = null

    val serverId: String?
        get() = if (title == null) null else title!!.toLowerCase()

    val username: String
        get() = title!!.substring(MARK_USERNAME.length)

    val isFavorite: Boolean
        get() = title != null && title!!.startsWith(MARK_USERNAME)

    companion object {

        private val MARK_USERNAME = "username:"

        fun makeFavorite(username: String): Tag {
            return object : Tag() {
                init {
                    title = MARK_USERNAME + username
                }
            }
        }

        fun makeFeatured(): Tag {
            return Tag()
        }
    }
}