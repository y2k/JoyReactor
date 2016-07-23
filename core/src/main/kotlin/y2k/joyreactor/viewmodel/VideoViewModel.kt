package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.getArgument
import y2k.joyreactor.common.property
import y2k.joyreactor.services.AttachmentService
import java.io.File

/**
 * Created by y2k on 3/8/16.
 */
class VideoViewModel(
    navigation: NavigationService,
    service: AttachmentService,
    scope: (String, () -> Unit) -> Unit) {

    val isBusy = property(true)
    val isError = property(false)
    val videoFile = property<File>()

    private val postId = navigation.getArgument<Long>()

    init {
        scope(service.downloadInBackground(postId)) {
            async_ {
                videoFile += await(service.getVideoFile(postId))
                service.getDownloadStatus(postId).let {
                    isBusy += it.isInProgress
                    isError += it.isFinishedWithError
                }
            }
        }
    }
}