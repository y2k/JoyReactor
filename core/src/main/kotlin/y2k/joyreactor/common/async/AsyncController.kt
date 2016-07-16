package y2k.joyreactor.common.async

/**
 * Created by y2k on 16/07/16.
 */

fun <T> asyncResult(coroutine c: ContinuationController<T>.() -> Continuation<Unit>): AsyncTask<T> {
    val controller = ContinuationController<T>()
    controller.c().resume(Unit)
    return controller.task
}

fun async(coroutine c: ContinuationController<Unit>.() -> Continuation<Unit>): AsyncTask<Unit> {
    val controller = ContinuationController<Unit>()
    controller.c().resume(Unit)
    return controller.task
}

@AllowSuspendExtensions
@Suppress("unused", "UNUSED_PARAMETER")
class ContinuationController<T> {

    val task: AsyncTask<T> = AsyncTask()

    operator fun handleResult(value: T, c: Continuation<Nothing>) {
        task.resume(value)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        task.resumeWithException(t)
    }

    suspend fun <T> await(f: AsyncTask<T>, machine: Continuation<T>) {
        f.whenComplete { value, throwable ->
            if (throwable == null) machine.resume(value!!)
            else machine.resumeWithException(throwable)
        }
    }
}