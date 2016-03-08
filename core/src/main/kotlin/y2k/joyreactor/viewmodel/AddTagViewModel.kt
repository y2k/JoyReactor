package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.UserService

/**
 * Created by y2k on 3/8/16.
 */
class AddTagViewModel(
    private val navigationService: NavigationService,
    private val service: UserService) {

    val isBusy = binding(false)
    val tag = binding("")
    val error = binding(false)

    fun add() {
        isBusy.value = true
        error.value = false
        service
            .addTag(tag.value)
            .subscribeOnMain({
                isBusy.value = false
                navigationService.closeAddTag()
            }, {
                it.printStackTrace()
                isBusy.value = false
                error.value = true
            })
    }
}