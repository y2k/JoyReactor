package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.NotAuthorizedException
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.getDocumentAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable

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

    @Deprecated("Use operator invoke")
    fun request(): Observable<String?> {
        return ioObservable {
            val document = httpClient.getDocument("http://joyreactor.cc/donate")
            document.select("a#settings").first()?.text()
        }
    }
}