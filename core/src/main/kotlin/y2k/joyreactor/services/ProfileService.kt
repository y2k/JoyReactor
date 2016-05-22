package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.model.Profile
import y2k.joyreactor.services.requests.LoginRequestFactory
import y2k.joyreactor.services.requests.ProfileRequestFactory

/**
 * Created by y2k on 11/25/15.
 */
class ProfileService(
    private val httpClient: HttpClient,
    private val profileRequestFactory: ProfileRequestFactory,
    private val loginRequestFactory: LoginRequestFactory) {

    fun login(username: String, password: String): Completable {
        return loginRequestFactory.request(username, password)
    }

    fun getProfile(): Observable<Profile> {
        return profileRequestFactory.request()
    }

    fun logout(): Observable<Unit> {
        return ioObservable { httpClient.clearCookies() }
    }

    fun isAuthorized(): Observable<Boolean> {
        return profileRequestFactory
            .request()
            .map { true }.onErrorReturn { false }
    }
}