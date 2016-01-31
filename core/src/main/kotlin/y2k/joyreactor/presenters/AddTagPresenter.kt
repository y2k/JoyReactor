package y2k.joyreactor.presenters

import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.TagListService

/**
 * Created by y2k on 08/10/15.
 */
class AddTagPresenter(
        private val view: AddTagPresenter.View,
        private val service: TagListService) {

    fun add(tag: String) {
        view.setIsBusy(true)

        service.addTag(tag).subscribe({
            view.setIsBusy(false)
            Navigation.instance.closeAddTag()
        }, { e ->
            e.printStackTrace()
            view.setIsBusy(false)
            view.showErrorMessage()
        })
    }

    interface View {

        fun setIsBusy(isBusy: Boolean)

        fun showErrorMessage()
    }
}