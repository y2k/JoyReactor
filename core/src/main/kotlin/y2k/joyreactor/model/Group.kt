package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
data class Group(
    @DatabaseField val type: Type = Type.Featured,
    @DatabaseField val name: String = "",
    @DatabaseField val quality: Quality = Quality.Good,
    @DatabaseField val title: String = "",
    @DatabaseField(dataType = com.j256.ormlite.field.DataType.SERIALIZABLE) val image: Image? = null,
    @DatabaseField val isVisible: Boolean = false,
    @DatabaseField(generatedId = true) override val id: Long = 0) : Serializable, Dto {

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