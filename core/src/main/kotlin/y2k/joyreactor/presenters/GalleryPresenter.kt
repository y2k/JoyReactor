package y2k.joyreactor.presenters

import y2k.joyreactor.Image
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 2/7/16.
 */
class GalleryPresenter(
    private val view: View,
    private val postService: PostService) {

    init {
        postService
            .getPostImages()
            .subscribeOnMain { view.update(it) }
    }


    interface View {

        fun update(images: List<Image>)
    }
}