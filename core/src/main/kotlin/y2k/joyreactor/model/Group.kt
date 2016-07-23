package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.common.strongHashCode
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
data class Group(
    @DatabaseField(id = true) override val id: Long = 0L,
    @DatabaseField val serverId: String = "",
    @DatabaseField val title: String = "",
    @DatabaseField(dataType = com.j256.ormlite.field.DataType.SERIALIZABLE) val image: Image? = null,
    @DatabaseField val isVisible: Boolean = false) : Serializable, Dto {

    val type: Type
        get() = Type.valueOf(serverId.split(":")[0])
    val name: String
        get() = serverId.split(":")[1]
    val quality: Quality
        get() = Quality.valueOf(serverId.split(":")[2])

    val username: String
        get() = name

    companion object {

        val Undefined = Group(Group.Type.Tag, "", Group.Quality.Good, "")

        fun makeFavorite(user: String): Group {
            return Group(Type.Favorite, user, Quality.Good, user)
        }

        fun makeFeatured(): Group {
            return Group(Type.Featured, "", Quality.Good, "")
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

fun Group(base: Group, quality: Group.Quality): Group {
    return Group(base.type, base.name, quality, base.title, base.image)
}

fun Group(type: Group.Type, name: String, quality: Group.Quality, title: String, image: Image? = null): Group {
    val serverId = "$type:$name:$quality"
    return Group(id = serverId.strongHashCode(), serverId = serverId, title = title, image = image)
}