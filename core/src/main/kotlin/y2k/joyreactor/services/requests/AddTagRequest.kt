package y2k.joyreactor.services.requests

import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import java.net.URLEncoder

/**
 * Created by y2k on 19/10/15.
 */
class AddTagRequest(private val httpClient: HttpClient) {

    fun request(tagName: String): CompletableContinuation<*> {
        return runAsync {
            val tagUrl = "http://joyreactor.cc/tag/" + URLEncoder.encode(tagName)
            val tagPage = httpClient.getDocument(tagUrl)
            val addTagLink = tagPage.select("a.change_favorite_link").first().absUrl("href")
            httpClient.getText(addTagLink)
        }
    }
}