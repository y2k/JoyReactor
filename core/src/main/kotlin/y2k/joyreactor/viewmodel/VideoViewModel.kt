package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.binding
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.PostService
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class VideoViewModel(
    private val navigationService: NavigationService,
    private val service: PostService) {

    val isBusy = binding(false)
    val videoFile = binding<File>()

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