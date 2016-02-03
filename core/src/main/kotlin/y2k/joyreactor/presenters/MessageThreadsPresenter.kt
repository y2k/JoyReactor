package y2k.joyreactor.presenters

import y2k.joyreactor.Message
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.UserMessagesService

/**
 * Created by y2k on 01/10/15.
 */
class MessageThreadsPresenter(
    view: MessageThreadsPresenter.View,
    val broadcastService: BroadcastService,
    service: UserMessagesService) {

    init {
        view.setIsBusy(true)
        service.getThreads()
            .subscribe({ threads ->
                view.setIsBusy(false)
                view.reloadData(threads)
            }, { it.printStackTrace() })
    }

    fun selectThread(thread: Message) {
        broadcastService.broadcast(BroadcastService.ThreadSelectedMessage(thread))
    }

    interface View {

        fun setIsBusy(isBusy: Boolean)

        fun reloadData(threads: List<Message>)
    }
}