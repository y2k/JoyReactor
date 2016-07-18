package y2k.joyreactor.common.async

/**
 * Created by y2k on 16/07/16.
 */

fun <T> async(coroutine c: ContinuationController<T>.() -> Continuation<Unit>): CompletableContinuation<T> {
    val controller = ContinuationController<T>()
    controller.c().resume(Unit)
    return controller.task
}

fun async_(coroutine c: ContinuationController<Unit>.() -> Continuation<Unit>): CompletableContinuation<Unit> {
    val controller = ContinuationController<Unit>()
    controller.c().resume(Unit)
    return controller.task
}

@AllowSuspendExtensions
@Suppress("unused", "UNUSED_PARAMETER")
class ContinuationController<T> {

    val task: CompletableContinuation<T> = CompletableContinuation()

    operator fun handleResult(value: T, c: Continuation<Nothing>) {
        task.resume(value)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        task.resumeWithException(t)
    }

    suspend fun <T> CompletableContinuation<T>.await_(machine: Continuation<CompletableContinuation.Result<T>>) {
        whenComplete_ { machine.resume(it) }
    }

    suspend fun <T> await(f: CompletableContinuation<T>, machine: Continuation<T>) {
        f.whenComplete_ {
            if (it.error == null) machine.resume(it.result!!)
            else machine.resumeWithException(it.error)
        }
    }

    suspend fun <T> runAsync(f: () -> T, machine: Continuation<T>) {
        runAsync(f).whenComplete_ {
            if (it.error == null) machine.resume(it.result!!)
            else machine.resumeWithException(it.error)
        }
    }

    suspend fun <T, R> CompletableContinuation<T>.than(f: (T) -> R, machine: Continuation<ExceptionalMonad<R>>) {
        whenComplete_ {
            val monad = if (it.error == null) ExceptionalMonad(it.result)
            else ExceptionalMonad(null, it.error)

            machine.resume(monad.than(f))
        }
    }
}