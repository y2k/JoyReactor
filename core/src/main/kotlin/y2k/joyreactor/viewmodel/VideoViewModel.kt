package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class VideoViewModel(service: PostService) {

    val isBusy = binding(false)
    val videoFile = binding<File>()

    init {
        isBusy.value = true
        service
            .getFromCache(NavigationService.instance.argument)
            .map { it.image!!.fullUrl("mp4") }
            .flatMap { OriginalImageRequestFactory().request(it) }
            .subscribeOnMain({
                videoFile.value = it
                isBusy.value = false
            }, {
                it.printStackTrace()
                isBusy.value = false
            })
    }
}