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

    public static Observable<Void> action(UnsafeAction0 action0) {
        return action(null, action0);
    }

    public static Observable<Void> action(Executor executor, UnsafeAction0 action0) {
        Scheduler scheduler = executor == null ? Schedulers.io() : Schedulers.from(executor);
        return Observable
                .<Void>create(subscriber -> {
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
        return func(null, action);
    }

    public static <T> Observable<T> func(Executor executor, Callable<T> action) {
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