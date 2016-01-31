package y2k.joyreactor

import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 10/1/15.
 */
class Message(
    val text: String,
    val date: Date,
    val isMine: Boolean,
    val userName: String,
    val userImage: String?
) : Serializable {

    fun getUserImageObject(): UserImage {
        return if (userImage == null) UserImage() else UserImage(userImage)
    }
}