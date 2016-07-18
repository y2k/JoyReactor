package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.getDocumentAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.postAsync

/**
 * Created by y2k on 9/30/15.
 */
class LoginRequestFactory(private val httpClient: HttpClient) {

    fun request(username: String, password: String): CompletableFuture<*> {
        return getSiteToken()
            .thenAsync {
                httpClient
                    .buildRequest()
                    .addField("signin[username]", username)
                    .addField("signin[password]", password)
                    .addField("signin[_csrf_token]", it)
                    .postAsync("http://joyreactor.cc/login")
            }
            .then { validateIsSuccessLogin(it) }
    }

    private fun getSiteToken(): CompletableFuture<String> {
        return httpClient
            .getDocumentAsync("http://joyreactor.cc/login")
            .then { it.getElementById("signin__csrf_token").attr("value") }
    }

    private fun validateIsSuccessLogin(mainPage: Document) {
        if (mainPage.getElementById("logout") == null)
            throw IllegalStateException()
    }
}