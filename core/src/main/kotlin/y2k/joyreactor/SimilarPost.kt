package y2k.joyreactor

import java.io.Serializable

/**
 * Created by y2k on 12/1/15.
 */
class SimilarPost(val postId: String) : Serializable {

    var id: Int = 0
    var parentPostId: Int = 0
    var image: Image? = null
}