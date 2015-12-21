package y2k.joyreactor.presenters

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
        service.getFromCache(Navigation.getInstance().argumentPostId)
                .map({ post -> post.image!!.fullUrl("mp4") })
                .flatMap({ url -> OriginalImageRequestFactory().request(url) })
                .subscribe({ videoFile ->
                    view.showVideo(videoFile)
                    view.setBusy(false)
                }) { e ->
                    e.printStackTrace()
                    view.setBusy(false)
                }
    }

    interface View {

        fun showVideo(videoFile: File)

        fun setBusy(isBusy: Boolean)
    }
}