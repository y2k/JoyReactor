package y2k.joyreactor.presenters

import y2k.joyreactor.model.Message
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserMessagesService
import y2k.joyreactor.services.requests.SendMessageRequest

/**
 * Created by y2k on 01/10/15.
 */
class MessagesPresenter(
    private val view: MessagesPresenter.View,
    private val service: UserMessagesService,
    private val lifeCycleService: LifeCycleService) {

    private var currentUsername: String? = null

    init {
        lifeCycleService.add(BroadcastService.ThreadSelectedMessage::class) {
            currentUsername = it.thread.userName
            reloadMessages()
        }
    }

    fun reply(message: String) {
        view.setBusy(true)
        view.clearMessage()
        currentUsername?.let {
            SendMessageRequest(it)
                .request(message)
                .subscribeOnMain(
                    { reloadMessages() },
                    { view.setBusy(false) })
        }
    }

    private fun reloadMessages() {
        currentUsername?.let {
            view.setBusy(true)
            service
                .getMessages(it)
                .subscribeOnMain({
                    view.updateMessages(it)
                    view.setBusy(false)
                }, {
                    view.setBusy(false)
                })
        }
    }

    interface View {

        fun updateMessages(messages: List<Message>)

        fun setBusy(isBusy: Boolean)

        fun clearMessage()
    }
}