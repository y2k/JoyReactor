package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Message
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.UserMessagesService

/**
 * Created by y2k on 2/23/16.
 */
class MessagesViewModel(
    private val navigation: NavigationService,
    private val service: UserMessagesService) {

    val messages = binding(emptyList<Message>())
    val newMessage = binding("")

    init {
        service
            .getMessages(navigation.argument)
            .subscribeOnMain { messages.value = it }
    }

    fun sendNewMessage() {
        service
            .sendNewMessage(navigation.argument, newMessage.value)
            .subscribeOnMain { messages.value = it }
        newMessage.value = ""
    }
}