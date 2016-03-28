package y2k.joyreactor.model

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable

/**
 * Created by y2k on 11/24/15.
 */
data class GroupPost(
    val groupId: Long,
    val postId: Long,
    override val id: Long = 0) : Serializable, DataSet.Dto {

    override fun identify(newId: Long): GroupPost {
        return copy(id = newId)
    }
}