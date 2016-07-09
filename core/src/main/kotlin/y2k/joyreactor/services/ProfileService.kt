package y2k.joyreactor.services

import rx.Completable
import rx.Single
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioCompletable
import y2k.joyreactor.model.Profile
import y2k.joyreactor.services.requests.LoginRequestFactory

/**
 * Created by y2k on 11/25/15.
 */
class ProfileService(
    private val httpClient: HttpClient,
    private val requestProfile: (String) -> Single<Profile>,
    private val requestMyName: () -> Single<String>,
    private val loginRequestFactory: LoginRequestFactory) {

    fun login(username: String, password: String): Completable {
        return loginRequestFactory.request(username, password)
    }

    fun getProfile(): Single<Profile> {
        return requestMyName()
            .flatMap { requestProfile(it) }
    }

    fun logout(): Completable {
        return ioCompletable { httpClient.clearCookies() }
    }

    fun isAuthorized(): Single<Boolean> {
        return requestMyName()
            .map { true }
            .onErrorReturn { false }
    }
}