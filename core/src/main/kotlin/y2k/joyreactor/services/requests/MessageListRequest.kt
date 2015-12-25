package y2k.joyreactor.services.requests

import y2k.joyreactor.Message
import y2k.joyreactor.http.HttpClient
import java.util.*

/**
 * Created by y2k on 11/17/15.
 */
class MessageListRequest {

    fun getMessages(): List<Message> {
        return messages
    }

    private val messages = ArrayList<Message>()

    var nextPage: String? = null
        private set

    fun execute(page: String?) {
        val url = page ?: "http://joyreactor.cc/private/list"
        val document = HttpClient.instance.getDocument(url)

        for (s in document.select("div.messages_wr > div.article")) {
            val m = Message()
            m.userName = s.select("div.mess_from > a").text()
            m.userImage = UserImageRequest(m.userName).execute()
            m.text = s.select("div.mess_text").text()
            m.date = Date(1000 * java.lang.Long.parseLong(s.select("span[data-time]").attr("data-time")))
            m.isMine = s.select("div.mess_reply").isEmpty()
            messages.add(m)
        }

        val nextNode = document.select("a.next").first()
        nextPage = nextNode?.absUrl("href")

        loadUserImages()
    }

    private fun loadUserImages() {
        for (m in messages)
            m.userImage = UserImageRequest(m.userName).execute()
    }
}