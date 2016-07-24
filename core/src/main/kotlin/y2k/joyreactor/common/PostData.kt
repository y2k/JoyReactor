package y2k.joyreactor.common

import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Post
import java.io.File

/**
 * Created by y2k on 24/07/16.
 */
data class PostData(
    val post: Post,
    val images: List<Image>,
    val topComments: List<Comment>,
    val poster: File?)