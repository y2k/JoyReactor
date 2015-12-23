package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Message
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.services.MemoryBuffer
import y2k.joyreactor.services.requests.MessageListRequest
import java.util.*

/**
 * Created by y2k on 11/17/15.
 */
class PrivateMessageFetcher(
        private val request: MessageListRequest,
        private val buffer: MemoryBuffer) {

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
                if (buffer.messages.none { it.isMine == true && it.date == mineOldest })
                    return true
            }
            if (theirOldest != null) {
                if (buffer.messages.none { it.isMine == false && it.date == theirOldest })
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
                buffer.messages = newMessages
        }

        private fun isNotInRepository(m: Message): Boolean {
            return buffer.messages.none { it.isMine == m.isMine && it.date == m.date }
        }
    }
}