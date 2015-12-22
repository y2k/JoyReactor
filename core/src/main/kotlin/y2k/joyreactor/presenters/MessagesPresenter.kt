package y2k.joyreactor.presenters

import rx.functions.Action1
import y2k.joyreactor.Message
import y2k.joyreactor.services.MessageService
import y2k.joyreactor.services.requests.SendMessageRequest

/**
 * Created by y2k on 01/10/15.
 */
class MessagesPresenter(
        private val view: MessagesPresenter.View,
        private val service: MessageService) : Presenter() {

    init {
        // FIXME:
        reloadMessages(getUsername())

        messages.add(
                Action1<MessageThreadsPresenter.ThreadSelectedMessage> { reloadMessages(it.thread.userName) },
                MessageThreadsPresenter.ThreadSelectedMessage::class.java)
    }

    fun reply(message: String) {
        view.setIsBusy(true)
        SendMessageRequest(getUsername())
                .request(message)
                .subscribe({ reloadMessages(getUsername()) }, { it.printStackTrace() })
    }

    private fun reloadMessages(username: String) {
        view.setIsBusy(true)
        service.getMessages(username)
                .subscribe({ messages ->
                    view.updateMessages(messages)
                    view.setIsBusy(false)
                }, { it.printStackTrace() })
    }

    // TODO:
    private fun getUsername(): String = "user500"

    interface View {

        fun updateMessages(messages: List<Message>)

        fun setIsBusy(isBusy: Boolean)
    }
}