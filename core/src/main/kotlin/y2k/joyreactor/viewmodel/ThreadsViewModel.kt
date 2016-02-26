package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Message
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserMessagesService

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
        lifeCycleService.add { refresh() }
    }

    fun refresh() {
        isBusy.value = true
        var isWeb = false // TODO: переделать
        service
            .getThreads()
            .subscribeOnMain {
                threads.value = it

                if (isWeb) isBusy.value = false
                else isWeb = true
            }
    }

    fun selectThread(index: Int) {
        navigation.openMessages(threads.value[index].userName)
    }
}