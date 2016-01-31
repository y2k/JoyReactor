package y2k.joyreactor.common

import rx.Observable

/**
 * Created by y2k on 1/31/16.
 */

fun <T> Observable<T>.peek(func: (T) -> Unit): Observable<T> {
    return map {
        func(it);
        it
    }
}