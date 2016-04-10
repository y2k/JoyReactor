package y2k.joyreactor.model

import y2k.joyreactor.services.repository.Dto
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 10/1/15.
 */
data class Message(
    val text: String,
    val date: Date,
    val isMine: Boolean,
    val userName: String,
    val userImage: String?,
    override val id: Long = 0) : Dto, Serializable {

    override fun identify(newId: Long): Message {
        return copy(id = newId)
    }

    fun getUserImageObject(): UserImage {
        return if (userImage == null) UserImage() else UserImage(userImage)
    }
}