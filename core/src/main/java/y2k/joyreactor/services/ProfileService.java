package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.services.requests.LoginRequest;

/**
 * Created by y2k on 11/25/15.
 */
public class ProfileService {

    public Observable<Void> login(String username, String password) {
        return new LoginRequest(username, password).request();
    }
}