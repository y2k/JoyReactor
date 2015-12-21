package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 10/2/15.
 */
class SendMessageRequest(private val username: String) {

    fun request(message: String): Observable<Void> {
        return ObservableUtils.action {
            HttpClient.getInstance()
                    .beginForm()
                    .put("username", username)
                    .put("text", message)
                    .putHeader("X-Requested-With", "XMLHttpRequest")
                    .putHeader("Referer", "http://joyreactor.cc/private/list")
                    .send("http://joyreactor.cc/private/create")
        }
    }
}