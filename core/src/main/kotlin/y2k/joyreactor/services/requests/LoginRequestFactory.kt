package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 9/30/15.
 */
class LoginRequestFactory(private val httpClient: HttpClient) {

    fun request(username: String, password: String): Observable<Unit> {
        return ioObservable {
            val doc = httpClient
                .beginForm()
                .put("signin[username]", username)
                .put("signin[password]", password)
                .put("signin[remember]", "on")
                .put("signin[_csrf_token]", getCsrf())
                .send("http://joyreactor.cc/login")

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