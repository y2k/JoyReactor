package y2k.joyreactor.common

import rx.Completable
import rx.Observable
import rx.Single
import rx.Subscription
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/31/16.
 */

fun <T> Single<T>.replaceIfNull(other: Single<T>): Single<T> {
    return flatMap {
        if (it != null) Single.just<T>(it)
        else other
    }
}

fun <T> Single<T>.toCompletable(): Completable {
    return Completable.fromSingle(this)
}

fun <T> Completable.andThen(single: Single<T>): Single<T> {
    return andThen(single.toObservable()).toSingle()
}

inline fun <T> Single<T>.subscribe(crossinline f: (T?, Throwable?) -> Unit): Subscription {
    return subscribe({ f(it, null) }, { f(null, it) })
}

fun <T> Observable<T>.peek(func: (T) -> Unit): Observable<T> {
    return map {
        func(it);
        it
    }
}

inline fun <T, R> Observable<T?>.mapNotNull(crossinline f: (T) -> R): Observable<R?> {
    return map { it?.let(f) }
}

inline fun <T, R> Single<T?>.mapNotNull(crossinline f: (T) -> R): Single<R?> {
    return map { it?.let(f) }
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

fun <T> Observable<T>.subscribeOnMain(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, onError)
}

fun <T> Observable<T>.subscribeOnMain(onNext: (T) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, { it.printStackTrace() })
}