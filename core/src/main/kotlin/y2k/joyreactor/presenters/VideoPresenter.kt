package y2k.joyreactor.presenters

import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.requests.OriginalImageRequestFactory

import java.io.File

/**
 * Created by y2k on 22/10/15.
 */
class VideoPresenter(view: VideoPresenter.View, service: PostService) {

    init {
        view.setBusy(true)
        service.getFromCache(Navigation.instance.argumentPostId)
            .map { it.image!!.fullUrl("mp4") }
            .flatMap { OriginalImageRequestFactory().request(it) }
            .subscribeOnMain({ videoFile ->
                view.showVideo(videoFile)
                view.setBusy(false)
            }, {
                it.printStackTrace()
                view.setBusy(false)
            })
    }

    interface View {

        fun showVideo(videoFile: File)

        fun setBusy(isBusy: Boolean)
    }
}