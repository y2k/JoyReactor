package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserMessagesService

/**
 * Created by y2k on 2/23/16.
 */
class ThreadsViewModel(
    scope: LifeCycleService,
    private val navigation: NavigationService,
    private val service: UserMessagesService) {

    val threads = property(emptyList<Message>())
    val isBusy = property(false)

    init {
        scope(service.syncInBackground()) {
            async_ {
                isBusy += service.isSyncInProgress()
                threads += await(service.getThreads())
            }
        }
    }

    fun selectThread(index: Int) {
        navigation.openVM<MessagesViewModel>(threads.value[index].userName)
    }
}