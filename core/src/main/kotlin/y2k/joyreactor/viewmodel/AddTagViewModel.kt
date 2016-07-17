package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
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
        async_ {
            isBusy += true
            error += false

            try {
                await(service.favoriteTag(tag.value))
                navigationService.close()
            } catch (e: Exception) {
                error += true
            }
            isBusy += false
        }
    }
}