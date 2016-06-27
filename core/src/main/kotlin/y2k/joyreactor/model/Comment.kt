package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
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
    @DatabaseField var replies: Int = 0,
    @DatabaseField(id = true) override val id: Long = 0
) : Dto, Serializable {

    override fun identify(newId: Long) = copy(id = newId)

    val userImageObject: UserImage
        get() = if (userImage == null) UserImage() else UserImage(userImage)

    fun setAttachmentObject(url: String, width: Int, height: Int) {
        attachmentObject = Image(url, width, height)
    }

    var attachmentObject: Image? = null
        private set
}