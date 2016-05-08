package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.buildRequest
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable

/**
 * Created by y2k on 9/30/15.
 */
class LoginRequestFactory(private val httpClient: HttpClient) {

    fun request(username: String, password: String): Observable<Unit> {
        return ioObservable {
            val doc = httpClient
                .buildRequest()
                .addField("signin[username]", username)
                .addField("signin[password]", password)
                .addField("signin[remember]", "on")
                .addField("signin[_csrf_token]", getCsrf())
                .post("http://joyreactor.cc/login")

            //            System.out.println(doc.html());

            // FIXME:
            //            if (doc.getElementById("logout") == null)
            //                throw new IllegalStateException();
        }
    }

    private fun getCsrf(): String {
        val document = httpClient.getDocument("http://joyreactor.cc/login")
        return document.getElementById("signin__csrf_token").attr("value")
    }
}