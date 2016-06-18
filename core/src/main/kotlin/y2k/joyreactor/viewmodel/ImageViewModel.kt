package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ui
import y2k.joyreactor.common.property
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.services.PostService
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class ImageViewModel(service: PostService) {

    val isBusy = property(false)
    val imageFile = property<File>()

    init {
        isBusy += true
        service
            .mainImage(NavigationService.instance.argument.toLong())
            .ui({
                imageFile += it
                isBusy += false
            }) {
                it.printStackTrace()
                isBusy += false
            }
    }
}