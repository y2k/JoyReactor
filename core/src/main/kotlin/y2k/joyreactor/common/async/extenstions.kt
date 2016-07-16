package y2k.joyreactor.common.async

import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities

/**
 * Created by y2k on 16/07/16.
 */

//fun <T> CompletableContinuation<ExceptionalMonad<T>>.onError(f: () -> T): CompletableContinuation<T> {
//    val result = CompletableContinuation<T>()
//    whenComplete { t, e ->
//        val s = t!!
//        if (s.error == null) result.resume(s.value!!)
//        else result.resume(f())
//    }
//    return result
//}
//
//fun <T, R> CompletableContinuation<ExceptionalMonad<T>>.then(f: (T) -> R): CompletableContinuation<ExceptionalMonad<R>> {
//    val result = CompletableContinuation<ExceptionalMonad<R>>()
//    whenComplete { t, e ->
//        val s = t!!
//        if (s.error == null) result.resume(ExceptionalMonad(f(s.value!!)))
//        else result.resume(ExceptionalMonad<R>(error = s.error))
//    }
//    return result
//}

fun <T, R> CompletableContinuation<T>.then_(f: (ExceptionalMonad<T>) -> R): CompletableContinuation<R> {
    val result = CompletableContinuation<R>()
    whenComplete_ {
        if (it.error == null) f(ExceptionalMonad(it.result))
        else f(ExceptionalMonad(error = it.error))
    }
    return result
}

fun <T, R> CompletableContinuation<T>.then(f: (T) -> R): CompletableContinuation<R> {
    val result = CompletableContinuation<R>()
    whenComplete { t, e ->
        if (e == null) result.resume(f(t!!))
        else result.resumeWithException(e)
    }
    return result
}

fun <T, R> CompletableContinuation<T>.thenAsync(entities: Entities, f: DataContext.(T) -> R): CompletableContinuation<R> {
    return thenAsync {
        entities.useAsync {
            f(it)
        }
    }
}

fun <T, R> CompletableContinuation<T>.thenAsync(f: (T) -> CompletableContinuation<out R>): CompletableContinuation<R> {
    val result = CompletableContinuation<R>()
    whenComplete { t, e ->
        if (e == null) {
            f(t!!).whenComplete { r2, e2 ->
                if (e2 == null) result.resume(r2!!)
                else result.resumeWithException(e2)
            }
        } else result.resumeWithException(e)
    }
    return result
}

fun <T> CompletableContinuation<T>.onError(f: () -> T): CompletableContinuation<T> {
    val result = CompletableContinuation<T>()
    whenComplete { t, e ->
        if (e == null) result.resume(t!!)
        else result.resume(f())
    }
    return result
}