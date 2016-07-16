package y2k.joyreactor.common.async

import y2k.joyreactor.common.ForegroundScheduler
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 16/07/16.
 */
class CompletableContinuation<T> {

    @Volatile var isFinished = false

    private @Volatile var result: T? = null
    private @Volatile var error: Throwable? = null
    private @Volatile var callback: ((T?, Throwable?) -> Unit)? = null

    val finishedWithError: Boolean
        get() = error != null

    fun resume(data: T) {
        synchronized(this) {
            isFinished = true
            result = data
            error = null

            callback?.invoke(result, error)
        }
    }

    fun resumeWithException(exception: Throwable) {
        synchronized(this) {
            isFinished = true
            result = null
            error = exception

            callback?.invoke(result, error)
        }
    }

    @Deprecated("")
    fun whenComplete(f: (T?, Throwable?) -> Unit) {
        synchronized(this) {
            if (isFinished) f(result, error)
            else callback = f
        }
    }

    fun whenComplete_(f: (Result<T>) -> Unit) {
        synchronized(this) {
            if (isFinished) f(Result(result, error))
            else callback = { r, e -> f(Result(r, e)) }
        }
    }

    data class Result<T>(val result: T?, val error: Throwable?)

    companion object {

        fun <T> just(value: T): CompletableContinuation<T> {
            return CompletableContinuation<T>().apply {
                isFinished = true
                result = value
            }
        }
    }
}

private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
private val CORE_POOL_SIZE = CPU_COUNT + 1
private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

private val THREAD_POOL_EXECUTOR = ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAXIMUM_POOL_SIZE,
    1, TimeUnit.SECONDS,
    LinkedBlockingQueue<Runnable>(128))


fun delay(timeSpanInMs: Long): CompletableContinuation<*> {
    return runAsync { Thread.sleep(timeSpanInMs) }
}

fun <T> runAsync(f: () -> T): CompletableContinuation<T> {
    return runAsync(THREAD_POOL_EXECUTOR, f)
}

fun <T> runAsync(executor: Executor, f: () -> T): CompletableContinuation<T> {
    val task = CompletableContinuation<T>()
    executor.execute {
        try {
            val result = f()
            ForegroundScheduler.instance.createWorker().schedule { task.resume(result) }
        } catch (e: Exception) {
            ForegroundScheduler.instance.createWorker().schedule { task.resumeWithException(e) }
        }
    }
    return task
}
