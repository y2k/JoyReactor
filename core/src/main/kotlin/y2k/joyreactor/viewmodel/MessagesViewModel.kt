package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.UserMessagesService

/**
 * Created by y2k on 2/23/16.
 */
class MessagesViewModel(
    private val navigation: NavigationService,
    private val service: UserMessagesService) {

    val messages = property(emptyList<Message>())
    val newMessage = property("")

    init {
        service
            .getMessages(navigation.argument)
            .ui { messages += it }
    }

    fun sendNewMessage() {
        service
            .sendNewMessage(navigation.argument, newMessage.value)
            .ui { messages += it }
        newMessage += ""
    }
}