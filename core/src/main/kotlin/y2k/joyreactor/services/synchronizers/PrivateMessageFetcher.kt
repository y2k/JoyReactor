package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.MessageListRequest
import java.util.*

/**
 * Created by y2k on 11/17/15.
 */
class PrivateMessageFetcher(
    private val request: MessageListRequest,
    private val entities: DataContext.Factory) {

    private var mineOldest: Date? = null
    private var theirOldest: Date? = null

    fun execute(): Observable<Unit> {
        return entities.applyUse {
            var nextPage: String? = null
            for (page in 1..MaxPages) {
                val (messages, next) = request.getMessages(nextPage)

                updateLastMessageDates(messages)
                val needLoadNext = isNeedLoadNext(Messages)

                messages
                    .filter { s -> Messages.none { it.isMine == s.isMine && it.date == s.date } }
                    .forEach { Messages.add(it) }

                if (next == null)
                    break
                if (!needLoadNext)
                    break
                nextPage = next
            }

            saveChanges()
        }
    }

    private fun updateLastMessageDates(messages: List<Message>) {
        mineOldest = null
        theirOldest = null
        for (m in messages) {
            if (m.isMine)
                mineOldest = m.date
            else
                theirOldest = m.date
        }
    }

    private fun isNeedLoadNext(messages: Iterable<Message>): Boolean {
        if (mineOldest != null) {
            if (messages.none { it.isMine == true && it.date == mineOldest })
                return true
        }
        if (theirOldest != null) {
            if (messages.none { it.isMine == false && it.date == theirOldest })
                return true
        }
        return false
    }

    companion object {

        val MaxPages = 10
    }
}