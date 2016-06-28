package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class ImageViewModel(
    navigation: NavigationService,
    imageRequest: OriginalImageRequestFactory) {

    val isBusy = property(false)
    val imageFile = property<File>()

    init {
        isBusy += true
        imageRequest(navigation.argument)
            .ui({
                imageFile += it
                isBusy += false
            }, {
                it.printStackTrace()
                isBusy += false
            })
    }
}