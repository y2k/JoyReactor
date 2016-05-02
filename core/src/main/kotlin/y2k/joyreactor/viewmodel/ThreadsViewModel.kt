package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.binding
import y2k.joyreactor.model.Message
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserMessagesService
import y2k.joyreactor.platform.open

/**
 * Created by y2k on 2/23/16.
 */
class ThreadsViewModel(
    private val lifeCycleService: LifeCycleService,
    private val navigation: NavigationService,
    private val service: UserMessagesService) {

    val threads = binding(emptyList<Message>())
    val isBusy = binding(false)

    init {
        lifeCycleService.register { refresh() }
    }

    fun refresh() {
        isBusy += true
        var isWeb = false // TODO: переделать
        service
            .getThreads()
            .await {
                threads += it

                if (isWeb) isBusy += false
                else isWeb = true
            }
    }

    fun selectThread(index: Int) {
        navigation.open<MessagesViewModel>(threads.value[index].userName)
    }
}