package y2k.joyreactor.common

import rx.Completable
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/31/16.
 */

fun <T> Observable<T>.peek(func: (T) -> Unit): Observable<T> {
    return map {
        func(it);
        it
    }
}

fun ioUnitObservable(func: () -> Unit): Observable<Unit> {
    return ioObservable(func)
}

fun <T> ioObservable(func: () -> T): Observable<T> {
    return Observable.create {
        Schedulers.io().createWorker().schedule {
            try {
                it.onNext(func())
                it.onCompleted()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }
}

fun <T, R> Observable<T>.concatAndRepeat(other: Observable<R>): Observable<T> {
    return concatWith(other.flatMap { this })
}

fun Completable.subscribeOnMain(onComplete: () -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onError, onComplete)
}

fun Completable.subscribeOnMain(onComplete: () -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe({ it.printStackTrace() }, onComplete)
}

fun <T> Observable<T>.subscribeOnMain(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, onError)
}

fun <T> Observable<T>.subscribeOnMain(onNext: (T) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, { it.printStackTrace() })
}