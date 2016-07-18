package y2k.joyreactor.services

import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.SendMessageRequest

/**
 * Created by y2k on 12/8/15.
 */
class UserMessagesService(
    private val sendRequest: SendMessageRequest,
    private val fetcher: () -> CompletableFuture<*>,
    private val entities: Entities,
    private val backgroundWorks: BackgroundWorks) {

    fun sendNewMessage(username: String, message: String): CompletableFuture<List<Message>> {
        return sendRequest
            .request(username, message)
            .thenAsync { fetcher() }
            .thenAsync { getMessages(username) }
    }

    fun getThreads(): CompletableFuture<List<Message>> {
        return entities.useAsync {
            Messages
                .groupBy("userName", orderProp = "date")
                .sortedByDescending { it.date }
        }
    }

    fun getMessages(username: String): CompletableFuture<List<Message>> {
        return entities.use {
            Messages
                .filter("userName" to username)
                .sortedByDescending { it.date }
        }
    }

    fun syncInBackground(): String {
        val key = "sync-messages"
        backgroundWorks.markWorkStarted(key)
        fetcher().thenAccept { backgroundWorks.markWorkFinished(key, it.errorOrNull) }
        return key
    }

    fun isSyncInProgress(): Boolean = backgroundWorks.getStatus("sync-messages").isInProgress
}