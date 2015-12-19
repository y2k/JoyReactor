package y2k.joyreactor.presenters

import y2k.joyreactor.Message
import y2k.joyreactor.services.MessageService

/**
 * Created by y2k on 01/10/15.
 */
class MessageThreadsPresenter(view: MessageThreadsPresenter.View, service: MessageService) {

    init {
        view.setIsBusy(true)
        service.getThreads()
                .subscribe({ threads ->
                    view.setIsBusy(false)
                    view.reloadData(threads)
                }, { it.printStackTrace() })
    }

    fun selectThread(index: Int) {
        // TODO:
        //        Messenger.getInstance().send(new ThreadSelectedMessage(threads.get(index)));
    }

    class ThreadSelectedMessage(internal val thread: Message)

    interface View {

        fun setIsBusy(isBusy: Boolean)

        fun reloadData(threads: List<Message>)
    }
}