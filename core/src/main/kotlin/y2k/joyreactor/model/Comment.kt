package y2k.joyreactor.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import java.io.Serializable

/**
 * Created by y2k on 28/09/15.
 */
data class Comment(
    @DatabaseField val text: String = "",
    @DatabaseField val userImage: String? = null,
    @DatabaseField val parentId: Long = 0,
    @DatabaseField val rating: Float = 0f,
    @DatabaseField val postId: Long = 0,
    @DatabaseField val level: Int = 0,
    @DatabaseField var replies: Int = 0, // TODO: сделать immutable
    @DatabaseField(dataType = DataType.SERIALIZABLE) val attachment: Image? = null,
    @DatabaseField(id = true) val id: Long = 0) : Serializable {

    val userImageObject: UserImage
        get() = if (userImage == null) UserImage() else UserImage(userImage)
}