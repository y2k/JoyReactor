package y2k.joyreactor.common.async

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 16/07/16.
 */
class AsyncTask<T> {

    private @Volatile var result: T? = null
    private @Volatile var error: Throwable? = null
    private @Volatile var isFinished = false
    private @Volatile var callback: ((T?, Throwable?) -> Unit)? = null

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

    fun whenComplete(f: (T?, Throwable?) -> Unit) {
        synchronized(this) {
            if (isFinished) f(result, error)
            else callback = f
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

fun <T> runAsync(f: () -> T): AsyncTask<T> {
    val task = AsyncTask<T>()
    THREAD_POOL_EXECUTOR.execute {
        try {
            task.resume(f())
        } catch (e: Exception) {
            task.resumeWithException(e)
        }
    }
    return task
}