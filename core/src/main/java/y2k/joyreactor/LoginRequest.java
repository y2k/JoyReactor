package y2k.joyreactor;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by y2k on 9/30/15.
 */
public class LoginRequest {

    public LoginRequest(String username, String password) {
        // TODO:
    }

    public Observable<Void> request() {
        return Observable.create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(null);
        }));
    }
}