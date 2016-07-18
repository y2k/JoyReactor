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

fun <T, R> CompletableFuture<T>.then_(f: (ExceptionalMonad<T>) -> R): CompletableFuture<R> {
    val result = CompletableFuture<R>()
    thenAccept {
        if (it.error == null) f(ExceptionalMonad(it.result))
        else f(ExceptionalMonad(error = it.error))
    }
    return result
}

fun <T, R> CompletableFuture<T>.then(f: (T) -> R): CompletableFuture<R> {
    val continuation = CompletableFuture<R>()
    thenAccept {
        if (it.error == null) {
            try {
                continuation.complete(f(it.result!!))
            } catch (e: Exception) {
                continuation.completeExceptionally(e)
            }
        } else continuation.completeExceptionally(it.error)
    }
    return continuation
}

fun <T, R> CompletableFuture<T>.thenAsync(entities: Entities, f: DataContext.(T) -> R): CompletableFuture<R> {
    return thenAsync {
        entities.useAsync {
            f(it)
        }
    }
}

fun <T, R> CompletableFuture<T>.thenAsync(f: (T) -> CompletableFuture<out R>): CompletableFuture<R> {
    val result = CompletableFuture<R>()
    thenAccept {
        if (it.error == null) {
            f(it.result!!).thenAccept { it2 ->
                if (it2.error == null) result.complete(it2.result!!)
                else result.completeExceptionally(it2.error)
            }
        } else result.completeExceptionally(it.error)
    }
    return result
}

fun <T> CompletableFuture<T>.onError(f: () -> T): CompletableFuture<T> {
    val result = CompletableFuture<T>()
    thenAccept {
        if (it.error == null) result.complete(it.result!!)
        else result.complete(f())
    }
    return result
}

fun <T> CompletableFuture<T>.onErrorAsync(f: () -> CompletableFuture<T>): CompletableFuture<T> {
    val result = CompletableFuture<T>()
    thenAccept {
        if (it.error == null) result.complete(it.result!!)
        else {
            f().thenAccept {
                if (it.error == null) result.complete(it.result!!)
                else result.completeExceptionally(it.error)
            }
        }
    }
    return result
}