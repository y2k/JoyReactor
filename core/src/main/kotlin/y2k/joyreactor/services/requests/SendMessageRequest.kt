package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ajax
import y2k.joyreactor.common.ioUnitObservable
import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 10/2/15.
 */
class SendMessageRequest(private val httpClient: HttpClient) {

    fun request(username: String, message: String): Observable<Unit> {
        return ioUnitObservable {
            httpClient
                .buildRequest()
                .addField("username", username)
                .addField("text", message)
                .ajax("http://joyreactor.cc/private/list")
                .post("http://joyreactor.cc/private/create")
        }
    }
}