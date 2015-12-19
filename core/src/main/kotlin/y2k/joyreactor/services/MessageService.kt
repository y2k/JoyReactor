package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Message
import y2k.joyreactor.services.repository.MessageForUserQuery
import y2k.joyreactor.services.repository.MessageThreadQuery
import y2k.joyreactor.services.repository.Repository
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher

/**
 * Created by y2k on 12/8/15.
 */
class MessageService(
        private val fetcher: PrivateMessageFetcher,
        private val repository: Repository<Message>) {

    fun getThreads(): Observable<List<Message>> {
        return getFromRepo().mergeWith(fetcher.execute().flatMap { getFromRepo() })
    }

    private fun getFromRepo(): Observable<List<Message>> {
        return repository.queryAsync(MessageThreadQuery())
    }

    fun getMessages(username: String): Observable<List<Message>> {
        return repository.queryAsync(MessageForUserQuery(username))
    }
}