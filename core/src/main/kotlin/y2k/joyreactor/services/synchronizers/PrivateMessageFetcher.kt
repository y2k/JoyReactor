package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.repository.DataSet
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.MessageListRequest
import java.util.*

/**
 * Created by y2k on 11/17/15.
 */
class PrivateMessageFetcher(
    private val request: MessageListRequest,
    private val entities: Entities) {

    private var mineOldest: Date? = null
    private var theirOldest: Date? = null

    fun execute(): Observable<Unit> {
        return entities.use {
            var nextPage: String? = null
            for (page in 1..MaxPages) {
                val (messages, next) = request.getMessages(nextPage)

                updateLastMessageDates(messages)
                val needLoadNext = isNeedLoadNext(Messages)

                messages
                    .filter { s -> Messages.filter("isMine" to s.isMine, "date" to s.date).none() }
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

    private fun isNeedLoadNext(messages: DataSet<Message>): Boolean {
        if (mineOldest != null) {
            if (messages.filter("isMine" to true, "date" to mineOldest).none())
                return true
        }
        if (theirOldest != null) {
            if (messages.filter ("isMine" to false, "date" to theirOldest).none())
                return true
        }
        return false
    }

    companion object {

        val MaxPages = 10
    }
}