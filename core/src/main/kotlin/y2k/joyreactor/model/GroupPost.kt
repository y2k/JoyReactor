package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable

/**
 * Created by y2k on 11/24/15.
 */
data class GroupPost(
    @DatabaseField val groupId: Long = 0,
    @DatabaseField val postId: Long = 0,
    @DatabaseField(generatedId = true) override val id: Long = 0) : Serializable, Dto