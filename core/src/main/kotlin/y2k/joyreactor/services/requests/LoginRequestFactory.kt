package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import rx.Completable
import rx.Single
import y2k.joyreactor.common.getDocumentAsync
import y2k.joyreactor.common.getDocumentAsync_
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.postAsync

/**
 * Created by y2k on 9/30/15.
 */
class LoginRequestFactory(private val httpClient: HttpClient) {

    fun request(username: String, password: String): Completable {
        return getSiteToken()
            .flatMap {
                httpClient
                    .buildRequest()
                    .addField("signin[username]", username)
                    .addField("signin[password]", password)
                    .addField("signin[_csrf_token]", it)
                    .postAsync("http://joyreactor.cc/login")
            }
            .doOnSuccess { validateIsSuccessLogin(it) }
            .toCompletable()
    }

    private fun getSiteToken(): Single<String> {
        return httpClient
            .getDocumentAsync_("http://joyreactor.cc/login")
            .map { it.getElementById("signin__csrf_token").attr("value") }
    }

    private fun validateIsSuccessLogin(mainPage: Document) {
        if (mainPage.getElementById("logout") == null)
            throw IllegalStateException()
    }
}