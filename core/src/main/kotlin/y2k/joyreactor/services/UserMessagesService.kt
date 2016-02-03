package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Message
import y2k.joyreactor.common.concatAndRepeat
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher

/**
 * Created by y2k on 12/8/15.
 */
class UserMessagesService(
    private val fetcher: PrivateMessageFetcher,
    private val entities: DataContext.Factory) {

    fun getThreads(): Observable<List<Message>> {
        return entities
            .applyUse {
                Messages
                    .groupBy { it.userName }
                    .map { it.value.maxBy { it.date }!! }
            }
            .concatAndRepeat(fetcher.execute())
    }

    fun getMessages(username: String): Observable<List<Message>> {
        return entities.applyUse {
            Messages
                .filter { it.userName == username }
                .sortedBy { it.date }
        }
    }
}