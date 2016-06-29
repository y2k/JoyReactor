package y2k.joyreactor.common

import rx.Completable
import rx.Observable
import rx.Single
import rx.Subscription
import rx.schedulers.Schedulers
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities

/**
 * Created by y2k on 1/31/16.
 */

inline fun <T, R> Observable<T>.mapDatabase(context: Entities, crossinline f: DataContext.(T) -> R): Observable<R> {
    return flatMap { data ->
        context.use { f(data) }
    }
}

fun <T> Single<T>.replaceIfNull(f: () -> Single<T>): Single<T> {
    return flatMap {
        if (it != null) Single.just<T>(it)
        else f()
    }
}

fun <T> Observable<T>.replaceIfNull(f: () -> Observable<T>): Observable<T> {
    return flatMap {
        if (it != null) Observable.just<T>(it)
        else f()
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

inline fun <T> Observable<T>.subscribe(crossinline f: (T?, Throwable?) -> Unit): Subscription {
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

fun <T, R> Single<T>.andThen(f: (T) -> Observable<R>): Observable<R> {
    return flatMapObservable(f)
}

fun <T> Observable<T>.andThen(f: (T) -> Completable): Completable {
    return flatMap { f(it).toObservable<T>() }.toCompletable()
}

fun ioCompletable(func: () -> Unit): Completable {
    return Completable.create {
        Schedulers.io().createWorker().schedule {
            try {
                func()
                it.onCompleted()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }
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

fun Completable.ui(onComplete: () -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onError, onComplete)
}

fun Completable.ui(onComplete: () -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe({ it.printStackTrace() }, onComplete)
}

fun <T> Observable<T>.ui(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, onError)
}

fun <T> Observable<T>.ui(onNext: (T) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onNext, { it.printStackTrace() })
}

fun <T> Pair<Single<T>, Notifications>.subscribe(lifeCycle: LifeCycleService, onNext: (T) -> Unit) {
    lifeCycle.scope(second) {
        first
            .observeOn(ForegroundScheduler.instance)
            .subscribe(onNext, Throwable::printStackTrace)
    }
}

fun <T> Single<T>.ui(onSuccess: (T) -> Unit, onFail: (Throwable) -> Unit) {
    observeOn(ForegroundScheduler.instance)
        .subscribe(onSuccess, onFail)
}

fun <T> Single<T>.ui(onSuccess: (T) -> Unit) {
    observeOn(ForegroundScheduler.instance)
        .subscribe(onSuccess, { it.printStackTrace() })
}

fun Completable.pack() = PackedCompletable(this)

class PackedCompletable(completable: Completable) {

    var isBusy: Boolean = true
    var finishedWithError: Boolean = false

    init {
        completable.ui(
            { isBusy = false },
            { it.printStackTrace(); finishedWithError = true })
    }
}