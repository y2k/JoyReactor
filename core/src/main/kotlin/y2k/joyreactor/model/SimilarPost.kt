package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable

/**
 * Created by y2k on 12/1/15.
 */
data class SimilarPost(
    @DatabaseField val similarPostId: Long = 0,
    @DatabaseField val parentPostId: Long = 0,
    @DatabaseField(dataType = com.j256.ormlite.field.DataType.SERIALIZABLE) val image: Image? = null,
    override val id: Long = 0
) : Dto, Serializable {

    override fun identify(newId: Long) = copy(id = newId)
}