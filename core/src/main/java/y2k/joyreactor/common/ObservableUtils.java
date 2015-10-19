package y2k.joyreactor.common;

import rx.Observable;
import rx.schedulers.Schedulers;
import y2k.joyreactor.common.ForegroundScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Created by y2k on 30/09/15.
 */
public class ObservableUtils {

    public static <T> Observable<T> create(Callable<T> action) {
        Observable<T> result = Observable
                .create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
                    try {
                        subscriber.onNext(action.call());
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }));
        return result.observeOn(ForegroundScheduler.getInstance());
    }

    public static <T> Observable<T> create(Callable<T> action, Executor executor) {
        Observable<T> result = Observable
                .create(subscriber -> executor.execute(() -> {
                    try {
                        subscriber.onNext(action.call());
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }));
        return result.observeOn(ForegroundScheduler.getInstance());
    }
}