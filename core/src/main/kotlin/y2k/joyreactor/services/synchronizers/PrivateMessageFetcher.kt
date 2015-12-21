package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Message
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.services.repository.MessageForDateQuery
import y2k.joyreactor.services.repository.Repository
import y2k.joyreactor.services.requests.MessageListRequest

import java.util.ArrayList
import java.util.Date

/**
 * Created by y2k on 11/17/15.
 */
class PrivateMessageFetcher(
        private val request: MessageListRequest,
        private val repository: Repository<Message>) {

    private var mineOldest: Date? = null
    private var theirOldest: Date? = null

    fun execute(): Observable<Void> {
        return ObservableUtils.action {
            while (true) {
                request.execute(request.nextPage)

                updateLastMessageDates()
                val needLoadNext = isNeedLoadNext

                NewMessageSaver().save(request.getMessages())

                if (request.nextPage == null)
                    break
                if (!needLoadNext)
                    break
            }
        }
    }

    private fun updateLastMessageDates() {
        mineOldest = null
        theirOldest = null
        for (m in request.getMessages()) {
            if (m.isMine)
                mineOldest = m.date
            else
                theirOldest = m.date
        }
    }

    private val isNeedLoadNext: Boolean
        get() {
            if (mineOldest != null) {
                if (repository.queryFirst(MessageForDateQuery(mineOldest, true)) == null)
                    return true
            }
            if (theirOldest != null) {
                if (repository.queryFirst(MessageForDateQuery(theirOldest, false)) == null)
                    return true
            }
            return false
        }

    internal inner class NewMessageSaver {

        fun save(messages: List<Message>) {
            val newMessages = ArrayList<Message>()
            for (m in messages)
                if (isNotInRepository(m))
                    newMessages.add(m)

            if (!newMessages.isEmpty())
                repository.insertAll(newMessages)
        }

        private fun isNotInRepository(m: Message): Boolean {
            return repository.queryFirst(MessageForDateQuery(m.date, m.isMine)) == null
        }
    }
}