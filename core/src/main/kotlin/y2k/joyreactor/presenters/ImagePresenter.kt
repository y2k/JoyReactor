package y2k.joyreactor.presenters

import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.PostService

import java.io.File

/**
 * Created by y2k on 10/25/15.
 */
class ImagePresenter(view: ImagePresenter.View, service: PostService) {

    init {
        view.setBusy(true)
        service.mainImage(Navigation.getInstance().argumentPostId)
                .subscribe({ imageFile ->
                    view.showImage(imageFile)
                    view.setBusy(false)
                }) { e ->
                    e.printStackTrace()
                    view.setBusy(false)
                }
    }

    interface View {

        fun setBusy(isBusy: Boolean)

        fun showImage(imageFile: File)
    }
}