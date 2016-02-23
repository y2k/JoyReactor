//package y2k.joyreactor.presenters
//
//import y2k.joyreactor.model.Message
//import y2k.joyreactor.common.subscribeOnMain
//import y2k.joyreactor.services.BroadcastService
//import y2k.joyreactor.services.UserMessagesService
//
///**
// * Created by y2k on 01/10/15.
// */
//class MessageThreadsPresenter(
//    private val view: MessageThreadsPresenter.View,
//    private val broadcastService: BroadcastService,
//    private val service: UserMessagesService) {
//
//    init {
//        view.setIsBusy(true)
//        service.getThreads()
//            .subscribeOnMain {
//                view.setIsBusy(false)
//                view.reloadData(it)
//            }
//    }
//
//    fun selectThread(thread: Message) {
//        broadcastService.broadcast(BroadcastService.ThreadSelectedMessage(thread))
//    }
//
//    interface View {
//
//        fun setIsBusy(isBusy: Boolean)
//
//        fun reloadData(threads: List<Message>)
//    }
//}