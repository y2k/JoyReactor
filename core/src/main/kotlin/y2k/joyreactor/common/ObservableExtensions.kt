package y2k.joyreactor.common

import rx.*
import rx.schedulers.Schedulers
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities

/**
 * Created by y2k on 1/31/16.
 */

inline fun <T, R> Observable<T>.mapEntities(context: Entities, crossinline f: DataContext.(T) -> R): Observable<R> {
    return flatMap { data -> context.use { f(data) } }
}

inline fun <T, R> Observable<T>.doEntities(context: Entities, crossinline f: DataContext.(T) -> R): Completable {
    return flatMap { data -> context.use { f(data) } }.toCompletable()
}

fun <T> Observable<T?>.switchIfNull(f: () -> Observable<T>): Observable<T> {
    return flatMap {
        if (it != null) Observable.just<T>(it)
        else f()
    }
}

inline fun <T> Single<T>.subscribe(crossinline f: (T?, Throwable?) -> Unit): Subscription {
    return subscribe({ f(it, null) }, { f(null, it) })
}

inline fun <T> Observable<T>.subscribe(crossinline f: (T?, Throwable?) -> Unit): Subscription {
    return subscribe({ f(it, null) }, { f(null, it) })
}

inline fun <T, R> Single<T?>.mapNotNull(crossinline f: (T) -> R): Single<R?> {
    return map { it?.let(f) }
}

fun ioUnitObservable(func: () -> Unit): Observable<Unit> {
    return ioObservable(func)
}

fun <T> Single<T>.andThen(f: (T) -> Completable): Completable {
    return toObservable().flatMap { f(it).toObservable<T>() }.toCompletable()
}

fun ioCompletable(func: () -> Unit): Completable = ioCompletable(Schedulers.io(), func)

fun ioCompletable(scheduler: Scheduler, func: () -> Unit): Completable {
    return Completable.create {
        scheduler.createWorker().schedule {
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

fun <T> ioSingle(func: () -> T): Single<T> = ioSingle(Schedulers.io(), func)

fun <T> ioSingle(scheduler: Scheduler, func: () -> T): Single<T> {
    return Single.create {
        scheduler.createWorker().schedule {
            try {
                it.onSuccess(func())
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }
}

fun <T, R> Observable<T>.concatAndRepeat(other: Observable<R>): Observable<T> {
    return concatWith(other.flatMap { this })
}

fun Completable.ui(): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe({ it.printStackTrace() }, {})
}

fun Completable.ui(onComplete: () -> Unit, onError: (Throwable) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe(onError, onComplete)
}

fun Completable.ui(onComplete: (Throwable?) -> Unit): Subscription {
    return observeOn(ForegroundScheduler.instance).subscribe({ onComplete(it) }, { onComplete(null) })
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

    @Volatile var isBusy: Boolean = true
    @Volatile var finishedWithError: Boolean = false

    init {
        completable
            .doOnCompleted { isBusy = false }
            .doOnError {
                it.printStackTrace()
                isBusy = false
                finishedWithError = true
            }
            .ui({}, {})
    }
}