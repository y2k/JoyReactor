package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Profile;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;
import y2k.joyreactor.services.requests.LoginRequestFactory;
import y2k.joyreactor.services.requests.ProfileRequestFactory;

/**
 * Created by y2k on 11/25/15.
 */
public class ProfileService {

    private ProfileRequestFactory profileRequestFactory;
    private LoginRequestFactory loginRequestFactory;

    public ProfileService(ProfileRequestFactory profileRequestFactory, LoginRequestFactory loginRequestFactory) {
        this.profileRequestFactory = profileRequestFactory;
        this.loginRequestFactory = loginRequestFactory;
    }

    public Observable<?> login(String username, String password) {
        return loginRequestFactory.request(username, password);
    }

    public Observable<Profile> get() {
        return profileRequestFactory.request();
    }

    public Observable<?> logout() {
        return ObservableUtils.create(() -> HttpClient.getInstance().clearCookies());
    }
}