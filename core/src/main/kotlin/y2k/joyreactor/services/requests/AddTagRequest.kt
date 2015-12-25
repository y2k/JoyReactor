package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient
import java.net.URLEncoder

/**
 * Created by y2k on 19/10/15.
 */
class AddTagRequest(private val tagName: String) {

    fun request(): Observable<Void> {
        return ObservableUtils.action {
            val tagUrl = "http://joyreactor.cc/tag/" + URLEncoder.encode(tagName)
            val tagPage = HttpClient.instance.getDocument(tagUrl)
            val addTagLink = tagPage.select("a.change_favorite_link").first().absUrl("href")
            HttpClient.instance.getText(addTagLink)
        }
    }
}