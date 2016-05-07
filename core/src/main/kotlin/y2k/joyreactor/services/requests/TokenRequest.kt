package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.http.HttpClient

/**
 * Created by y2k on 4/26/16.
 */
class TokenRequest(private val httpClient: HttpClient) {

    fun request(): Observable<String> {
        return ioObservable {
            httpClient
                .getText("http://joyreactor.cc/donate")
                .let { tokenRegex.find(it)!!.groupValues[1] }
        }
    }

    companion object {

        private val tokenRegex = Regex("var token = '(.+?)'")
    }
}