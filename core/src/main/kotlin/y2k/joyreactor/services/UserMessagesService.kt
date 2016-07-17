package y2k.joyreactor.services

import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.SendMessageRequest
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher

/**
 * Created by y2k on 12/8/15.
 */
class UserMessagesService(
    private val sendRequest: SendMessageRequest,
    private val fetcher: PrivateMessageFetcher,
    private val entities: Entities) {

    fun sendNewMessage(username: String, message: String): CompletableContinuation<List<Message>> {
        return sendRequest
            .request(username, message)
            .thenAsync { fetcher.execute() }
            .thenAsync { getMessages(username) }
    }

    fun getThreads(): CompletableContinuation<List<Message>> {
        return entities
            .use {
                Messages
                    .groupBy("userName", orderProp = "date")
                    .sortedByDescending { it.date }
            }
            .concatAndRepeat(fetcher.execute())
    }

    fun getMessages(username: String): CompletableContinuation<List<Message>> {
        return entities.use {
            Messages
                .filter("userName" to username)
                .sortedByDescending { it.date }
        }
    }
}