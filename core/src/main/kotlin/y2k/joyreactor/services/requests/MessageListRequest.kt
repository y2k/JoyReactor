package y2k.joyreactor.services.requests

import y2k.joyreactor.Message
import y2k.joyreactor.http.HttpClient
import java.util.*

/**
 * Created by y2k on 11/17/15.
 */
class MessageListRequest(
    private val imageRequest: UserImageRequest) {

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
            val username = s.select("div.mess_from > a").text()
            messages.add(
                Message(
                    s.select("div.mess_text").text(),
                    Date(1000 * java.lang.Long.parseLong(s.select("span[data-time]").attr("data-time"))),
                    s.select("div.mess_reply").isEmpty(),
                    username,
                    imageRequest.execute(username)))
        }

        val nextNode = document.select("a.next").first()
        nextPage = nextNode?.absUrl("href")
    }
}