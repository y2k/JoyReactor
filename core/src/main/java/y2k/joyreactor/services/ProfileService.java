package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Profile;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;
import y2k.joyreactor.services.requests.LoginRequest;
import y2k.joyreactor.services.requests.ProfileRequest;

/**
 * Created by y2k on 11/25/15.
 */
public class ProfileService {

    public Observable<Void> login(String username, String password) {
        return new LoginRequest(username, password).request();
    }

    public Observable<Profile> get() {
        return new ProfileRequest().request();
    }

    public Observable<?> logout() {
        return ObservableUtils.create(() -> HttpClient.getInstance().clearCookies());
    }
}