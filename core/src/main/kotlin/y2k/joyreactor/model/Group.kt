package y2k.joyreactor.model

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
data class Group(
    val type: Type,
    val name: String,
    val quality: Quality,
    val title: String,
    val isVisible: Boolean = false,
    val image: Image? = null,
    override val id: Long = 0) : Serializable, DataSet.Dto {

    override fun identify(newId: Long): Group {
        return copy(id = newId)
    }

    val serverId: String
        get() = "$type:$name:$quality"

    val username: String
        get() = name

    val isFavorite: Boolean
        get() = type == Type.Favorite

    companion object {

        fun makeUsers(user: String): Group {
            return Group(Type.User, user, Quality.Good, user)
        }

        fun makeFavorite(user: String): Group {
            return Group(Type.Favorite, user, Quality.Good, user)
        }

        fun makeFeatured(): Group {
            return Group(Type.Favorite, "", Quality.Good, "")
        }
    }

    enum class Type {
        Featured, Tag, Favorite, User
    }

    enum class Quality {
        Good, Best, All
    }
}