package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.await
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
        isBusy += true
        error += false
        service
            .favoriteTag(tag.value)
            .await({
                isBusy += false
                navigationService.closeAddTag()
            }, {
                it.printStackTrace()
                isBusy += false
                error += true
            })
    }
}