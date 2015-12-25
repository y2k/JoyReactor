package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 10/4/15.
 */
class UserNameRequest {

    fun request(): Observable<String> {
        return ObservableUtils.func<String> {
            val document = HttpClient.instance.getDocument("http://joyreactor.cc/donate")
            val node = document.select("a#settings").first()
            node?.text()
        }
    }
}