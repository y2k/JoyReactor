package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import java.io.Serializable
import java.util.*

/**
 * Created by y2k on 10/1/15.
 */
data class Message(
    @DatabaseField val text: String = "",
    @DatabaseField val date: Date = Date(),
    @DatabaseField val isMine: Boolean = false,
    @DatabaseField val userName: String = "",
    @DatabaseField val userImage: String? = null,
    @DatabaseField(generatedId = true) val id: Long = 0) : Serializable {

    fun getUserImageObject(): UserImage {
        return if (userImage == null) UserImage() else UserImage(userImage)
    }
}