package y2k.joyreactor.common.async

/**
 * Created by y2k on 16/07/16.
 */

fun <T> async(coroutine c: ContinuationController<T>.() -> Continuation<Unit>): CompletableFuture<T> {
    val controller = ContinuationController<T>()
    controller.c().resume(Unit)
    return controller.task
}

fun async_(coroutine c: ContinuationController<Unit>.() -> Continuation<Unit>): CompletableFuture<Unit> {
    val controller = ContinuationController<Unit>()
    controller.c().resume(Unit)
    return controller.task
}

fun async__(coroutine c: ContinuationController<Unit>.() -> Continuation<Unit>) {
    val controller = ContinuationController<Unit>()
    controller.c().resume(Unit)
    controller.task.thenAccept { if (!it.isSuccess) throw it.error }
}

@AllowSuspendExtensions
@Suppress("unused", "UNUSED_PARAMETER")
class ContinuationController<T> {

    val task: CompletableFuture<T> = CompletableFuture()

    operator fun handleResult(value: T, c: Continuation<Nothing>) {
        task.complete(value)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        task.completeExceptionally(t)
    }

    suspend fun <T> CompletableFuture<T>.await_(machine: Continuation<CompletableFuture.Result<T>>) {
        thenAccept { machine.resume(it) }
    }

    suspend fun <T> await(f: CompletableFuture<T>, machine: Continuation<T>) {
        f.thenAccept {
            if (it.isSuccess) machine.resume(it.result)
            else machine.resumeWithException(it.error)
        }
    }

    suspend fun <T> runAsync(f: () -> T, machine: Continuation<T>) {
        runAsync(f).thenAccept {
            if (it.isSuccess) machine.resume(it.result)
            else machine.resumeWithException(it.error)
        }
    }

    suspend fun <T, R> CompletableFuture<T>.than(f: (T) -> R, machine: Continuation<ExceptionalMonad<R>>) {
        thenAccept {
            val monad = if (it.isSuccess) ExceptionalMonad(it.result)
            else ExceptionalMonad(null, it.error)

            machine.resume(monad.than(f))
        }
    }
}