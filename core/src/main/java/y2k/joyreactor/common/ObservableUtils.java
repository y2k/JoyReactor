package y2k.joyreactor.common;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Created by y2k on 30/09/15.
 */
public class ObservableUtils {

    public static Observable<?> action(UnsafeAction0 action0) {
        return action(action0, null);
    }

    public static Observable<?> action(UnsafeAction0 action0, Executor executor) {
        Scheduler scheduler = executor == null ? Schedulers.io() : Schedulers.from(executor);
        return Observable
                .create(subscriber -> {
                    try {
                        action0.call();
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(scheduler)
                .observeOn(ForegroundScheduler.getInstance());
    }

    public static <T> Observable<T> func(Callable<T> action) {
        return func(action, null);
    }

    public static <T> Observable<T> func(Callable<T> action, Executor executor) {
        Scheduler scheduler = executor == null ? Schedulers.io() : Schedulers.from(executor);
        return Observable
                .<T>create(subscriber -> {
                    try {
                        subscriber.onNext(action.call());
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(scheduler)
                .observeOn(ForegroundScheduler.getInstance());
    }

    @Deprecated
    public static Observable<Void> create(UnsafeAction0 action0) {
        return ObservableUtils.create(() -> {
            action0.call();
            return null;
        });
    }

    @Deprecated
    public static Observable<Void> create(UnsafeAction0 action0, Executor executor) {
        return ObservableUtils.create(() -> {
            action0.call();
            return null;
        }, executor);
    }

    @Deprecated
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

    @Deprecated
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

    public interface UnsafeAction0 {

        void call() throws Exception;
    }
}