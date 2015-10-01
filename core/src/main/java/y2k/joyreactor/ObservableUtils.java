package y2k.joyreactor;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.Callable;

/**
 * Created by y2k on 30/09/15.
 */
public class ObservableUtils {

    public static <T> Observable<T> create(Callable<T> action) {
        Observable<T> result = Observable
                .create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
                    try {
                        subscriber.onNext(action.call());
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }));
        return result.observeOn(ForegroundScheduler.getInstance());
    }
}