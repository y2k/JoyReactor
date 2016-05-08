package y2k.joyreactor.services.requests

import rx.Completable
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioUnitObservable
import java.net.URLEncoder

/**
 * Created by y2k on 19/10/15.
 */
class AddTagRequest(private val httpClient: HttpClient) {

    fun request(tagName: String): Completable {
        return ioUnitObservable {
            val tagUrl = "http://joyreactor.cc/tag/" + URLEncoder.encode(tagName)
            val tagPage = httpClient.getDocument(tagUrl)
            val addTagLink = tagPage.select("a.change_favorite_link").first().absUrl("href")
            httpClient.getText(addTagLink)
        }.toCompletable()
    }
}