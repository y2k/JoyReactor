package y2k.joyreactor.services.requests

import rx.Single
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.getTextAsync

/**
 * Created by y2k on 4/26/16.
 */
class TokenRequest(private val httpClient: HttpClient) {

    fun request(): Single<String> {
        return httpClient
            .getTextAsync("http://joyreactor.cc/donate")
            .map { tokenRegex.find(it)!!.groupValues[1] }
    }

    companion object {

        private val tokenRegex = Regex("var token = '(.+?)'")
    }
}