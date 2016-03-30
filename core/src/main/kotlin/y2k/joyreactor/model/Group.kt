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
    val image: Image? = null,
    val isVisible: Boolean = false,
    override val id: Long = 0) : Serializable, DataSet.Dto {

    override fun identify(newId: Long): Group {
        return copy(id = newId)
    }

    val serverId: String
        get() = "$type:$name:$quality"

    val username: String
        get() = name

    companion object {

        val Undefined = Group(Group.Type.Tag, "", Group.Quality.Good, "")

        fun makeFavorite(user: String): Group {
            return Group(Type.Favorite, user, Quality.Good, user)
        }

        fun makeFeatured(): Group {
            return Group(Type.Favorite, "", Quality.Good, "")
        }

        fun makeTag(name: String, image: Image? = null): Group {
            return Group(Type.Tag, name, Quality.Good, name, image)
        }
    }

    enum class Type {
        Featured, Tag, Favorite, User
    }

    enum class Quality {
        Good, Best, All
    }
}