package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ui
import y2k.joyreactor.common.property
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.services.UserService

/**
 * Created by y2k on 3/8/16.
 */
class AddTagViewModel(
    private val navigationService: NavigationService,
    private val service: UserService) {

    val isBusy = property(false)
    val tag = property("")
    val error = property(false)

    fun add() {
        isBusy += true
        error += false
        service
            .favoriteTag(tag.value)
            .ui({
                isBusy += false
                navigationService.close()
            }, {
                it.printStackTrace()
                isBusy += false
                error += true
            })
    }
}