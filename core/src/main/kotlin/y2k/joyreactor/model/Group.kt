package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import java.io.Serializable

/**
 * Created by y2k on 9/26/15.
 */
data class Group(
    @DatabaseField(id = true) val id: String = "",
    @DatabaseField val title: String = "",
    @DatabaseField(dataType = com.j256.ormlite.field.DataType.SERIALIZABLE) val image: Image? = null,
    @DatabaseField val isVisible: Boolean = false) : Serializable {

    val type: Type
        get() = Type.values()[id.substring(0..0).toInt()]
    val quality: Quality
        get() = Quality.values()[id.substring(1..1).toInt()]
    val username: String
        get() = name
    val name: String
        get() = id.substring(2)

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

    enum class Type { Featured, Tag, Favorite, User }
    enum class Quality { Good, Best, All }
}

fun Group(base: Group, quality: Group.Quality): Group {
    return Group(base.type, base.name, quality, base.title, base.image)
}

fun Group(type: Group.Type, name: String, quality: Group.Quality, title: String, image: Image? = null): Group {
    return Group(id = "${type.ordinal}${quality.ordinal}$name", title = title, image = image)
}