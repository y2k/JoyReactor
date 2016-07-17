package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.PostService

/**
 * Created by y2k on 3/8/16.
 */
class GalleryViewModel(
    private val navigation: NavigationService,
    private val service: PostService) {

    val images = property(emptyList<Image>())

    init {
        images += service.getImages(navigation.argument.toLong())
    }

    fun openImage(index: Int) {
        val imgUrl = images.value[index].fullUrl()
        navigation.openVM<ImageViewModel>(imgUrl)
    }
}