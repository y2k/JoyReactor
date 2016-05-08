package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.property
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.PostService
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class VideoViewModel(
    private val navigationService: NavigationService,
    private val service: PostService) {

    val isBusy = property(false)
    val videoFile = property<File>()

    init {
        isBusy += true
        service
            .getVideo(navigationService.argument)
            .await({
                videoFile += it
                isBusy += false
            }, {
                it.printStackTrace()
                isBusy += false
            })
    }
}