package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.ui
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.PostService
import y2k.joyreactor.common.platform.open

/**
 * Created by y2k on 3/8/16.
 */
class GalleryViewModel(
    private val navigation: NavigationService,
    private val service: PostService) {

    val images = property(emptyList<Image>())

    init {
        images += service.getPostImages(navigation.argument.toLong())
    }

    fun openImage(index: Int) {
        val imgUrl = images.value[index].fullUrl()
        navigation.open<ImageViewModel>(imgUrl)
    }
}