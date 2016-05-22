package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 9/27/15.
 */
data class Post(
    @DatabaseField val title: String = "",
    @DatabaseField(dataType = com.j256.ormlite.field.DataType.SERIALIZABLE) val image: Image? = null,
    @DatabaseField val userImage: String = "",
    @DatabaseField val userName: String = "",
    @DatabaseField val created: Date = Date(),
    @DatabaseField val commentCount: Int = 0,

    @DatabaseField val rating: Float = 0f,
    @DatabaseField val myLike: MyLike = MyLike.Like,

    val tags: List<String> = emptyList(),
    @DatabaseField(id = true) override val id: Long = 0) : Serializable, Comparable<Post>, Dto {

    fun imageAspectOrDefault(default: Float): Float {
        return image?.aspect ?: default
    }

    override fun identify(newId: Long): Post {
        throw UnsupportedOperationException()
    }

    // TODO:
    fun getUserImage2(): UserImage {
        return UserImage.fromUrl(userImage)
    }

    override fun compareTo(other: Post): Int {
        return (id - other.id).toInt()
    }
}

enum class MyLike {
    Unknown, Like, Dislike, Blocked
}