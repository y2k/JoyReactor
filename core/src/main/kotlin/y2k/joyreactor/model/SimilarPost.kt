package y2k.joyreactor.model

import com.j256.ormlite.field.DatabaseField
import y2k.joyreactor.services.repository.Dto
import java.io.Serializable
import com.j256.ormlite.field.DataType

/**
 * Created by y2k on 12/1/15.
 */
data class SimilarPost(
    @DatabaseField val parentPostId: Long = 0,
    @DatabaseField(dataType = DataType.SERIALIZABLE) val image: Image? = null,
    @DatabaseField(id = true) override val id: Long = 0
) : Dto, Serializable {

    override fun identify(newId: Long) = copy(id = newId)
}