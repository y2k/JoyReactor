package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Profile
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.http.HttpClient
import y2k.joyreactor.services.requests.LoginRequestFactory
import y2k.joyreactor.services.requests.ProfileRequestFactory

/**
 * Created by y2k on 11/25/15.
 */
class ProfileService(
    private val profileRequestFactory: ProfileRequestFactory,
    private val loginRequestFactory: LoginRequestFactory) {

    fun login(username: String, password: String): Observable<Unit> {
        return loginRequestFactory.request(username, password)
    }

    fun getProfile(): Observable<Profile> {
        return profileRequestFactory.request()
    }

    fun logout(): Observable<Unit> {
        return ioObservable { HttpClient.instance.clearCookies() }
    }

    fun isAuthorized(): Observable<Boolean> {
        return profileRequestFactory
            .request()
            .map { true }.onErrorReturn { false }
    }
}