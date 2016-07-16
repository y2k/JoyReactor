package y2k.joyreactor.services.requests

import y2k.joyreactor.common.NotAuthorizedException
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.getDocumentAsync
import y2k.joyreactor.common.http.HttpClient

/**
 * Created by y2k on 10/4/15.
 */
class UserNameRequest(
    private val httpClient: HttpClient) : Function0<CompletableContinuation<String>> {

    override operator fun invoke(): CompletableContinuation<String> {
        return httpClient
            .getDocumentAsync("http://joyreactor.cc/donate")
            .then {
                val nameElement = it.select("a#settings").first()
                nameElement?.text() ?: throw NotAuthorizedException()
            }
    }
}