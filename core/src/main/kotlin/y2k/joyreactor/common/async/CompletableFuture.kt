package y2k.joyreactor.common.async

import y2k.joyreactor.common.executeOnUi
import java.util.concurrent.*

/**
 * Created by y2k on 16/07/16.
 */
class CompletableFuture<T> {

    @Volatile var isFinished = false

    private @Volatile var result: T? = null
    private @Volatile var error: Throwable? = null
    private @Volatile var callback: ((T?, Throwable?) -> Unit)? = null

    val finishedWithError: Boolean
        get() = error != null

    fun complete(data: T) {
        synchronized(this) {
            isFinished = true
            result = data
            error = null

            callback?.invoke(result, error)
        }
    }

    fun completeExceptionally(exception: Throwable) {
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

    fun thenAccept(f: (Result<T>) -> Unit) {
        synchronized(this) {
            if (isFinished) f(Result(result, error))
            else callback = { r, e -> f(Result(r, e)) }
        }
    }

    data class Result<T>(val result: T?, val error: Throwable?)

    companion object {

        fun <T> just(value: T): CompletableFuture<T> {
            return CompletableFuture<T>().apply {
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


fun delay(timeSpanInMs: Long): CompletableFuture<*> {
    return runAsync { Thread.sleep(timeSpanInMs) }
}

fun <T> runAsync(f: () -> T): CompletableFuture<T> {
    return runAsync(THREAD_POOL_EXECUTOR, f)
}

fun <T> just(value: T): CompletableFuture<T> = CompletableFuture.just(value)

fun <T> runAsync(executor: Executor, f: () -> T): CompletableFuture<T> {
    val task = CompletableFuture<T>()
    executor.execute {
        try {
            val result = f()
            executeOnUi { task.complete(result) }
        } catch (e: Exception) {
            executeOnUi { task.completeExceptionally(e) }
        }
    }
    return task
}