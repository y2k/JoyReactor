package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Message
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher
import java.util.*

/**
 * Created by y2k on 12/8/15.
 */
class MessageService(
    private val fetcher: PrivateMessageFetcher,
    private val buffer: MemoryBuffer) {

    fun getThreads(): Observable<List<Message>> {
        return getFromRepo().mergeWith(fetcher.execute().flatMap { getFromRepo() })
    }

    private fun getFromRepo(): Observable<List<Message>> {
        val usersAlreadyAdded = HashSet<String>()
        val result = buffer.messages.filter { message ->
            if (usersAlreadyAdded.contains(message.userName))
                false
            else {
                usersAlreadyAdded.add(message.userName)
                true
            }
        }
        return Observable.just(result)
    }

    fun getMessages(username: String): Observable<List<Message>> {
        val result = buffer.messages.filter { it.userName == username }.sortedBy { it.date }
        return Observable.just(result)
    }
}