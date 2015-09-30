package y2k.joyreactor;

import rx.Observable;

/**
 * Created by y2k on 9/30/15.
 */
public class LoginRequest {

    String username;
    String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Observable<Void> request() {
        return ObservableUtils.create(() -> {
            throw new Exception(); // TODO:
        });
    }
}