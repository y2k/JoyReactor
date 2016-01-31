package y2k.joyreactor.common

import rx.Observable
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