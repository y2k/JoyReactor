package y2k.joyreactor.model

import java.io.Serializable

/**
 * Created by y2k on 28/09/15.
 */
data class Comment(
    val text: String,
    val userImage: String?,
    val id: Long,
    val parentId: Long,
    val rating: Float,
    val postId: Long,
    val level: Int = 0,
    var replies: Int = 0) : Serializable {

    val userImageObject: UserImage
        get() = if (userImage == null) UserImage() else UserImage(userImage)

    fun setAttachmentObject(url: String, width: Int, height: Int) {
        attachmentObject = Image(url, width, height)
    }

    var attachmentObject: Image? = null
        private set
}