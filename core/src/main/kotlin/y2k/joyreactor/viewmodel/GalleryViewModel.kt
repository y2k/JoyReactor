package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 3/8/16.
 */
class GalleryViewModel(private val postService: PostService) {

    val images = binding(emptyList<Image>())

    init {
        postService
            .getPostImages()
            .subscribeOnMain { images.value = it }
    }
}