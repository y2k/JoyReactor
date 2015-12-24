package y2k.joyreactor

import y2k.joyreactor.services.repository.DataSet
import java.io.Serializable

/**
 * Created by y2k on 11/24/15.
 */
class TagPost(val tagId: Long, val postId: Long) : Serializable, DataSet.Dto {

    override var id: Long = 0
}