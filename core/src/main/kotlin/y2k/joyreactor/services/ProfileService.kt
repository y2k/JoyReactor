package y2k.joyreactor.services

import rx.Completable
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.onError
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioCompletable
import y2k.joyreactor.model.Profile
import y2k.joyreactor.services.requests.LoginRequestFactory

/**
 * Created by y2k on 11/25/15.
 */
class ProfileService(
    private val httpClient: HttpClient,
    private val requestProfile: (String) -> CompletableContinuation<Profile>,
    private val loginRequestFactory: LoginRequestFactory,
    private val requestMyName: () -> CompletableContinuation<String>
) {

    fun login(username: String, password: String): Completable {
        return loginRequestFactory.request(username, password)
    }

    fun getProfile(): CompletableContinuation<Profile> {
        return requestMyName()
            .thenAsync { requestProfile(it) }
    }

    fun logout(): Completable {
        return ioCompletable { httpClient.clearCookies() }
    }

    fun isAuthorized(): CompletableContinuation<Boolean> {
        return requestMyName().then { true }.onError { false }
    }
}