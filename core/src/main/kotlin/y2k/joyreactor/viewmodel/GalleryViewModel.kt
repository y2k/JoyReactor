package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ui
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 3/8/16.
 */
class GalleryViewModel(private val postService: PostService) {

    val images = property(emptyList<Image>())

    init {
        postService
            .getPostImages()
            .ui { images += it }
    }
}