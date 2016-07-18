package y2k.joyreactor.common.async

import y2k.joyreactor.common.executeOnUi
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 16/07/16.
 */
class CompletableFuture<T> {

    private @Volatile var result: Result<T>? = null
    private @Volatile var callback: ((Result<T>) -> Unit)? = null

    fun complete(data: T) {
        synchronized(this) {
            val r = Result(data)
            result = r
            callback?.invoke(r)
        }
    }

    fun completeExceptionally(exception: Throwable) {
        synchronized(this) {
            val r = Result<T>(null, exception)
            result = r
            callback?.invoke(r)
        }
    }

    fun thenAccept(f: (Result<T>) -> Unit) {
        synchronized(this) {
            val r = result
            if (r == null) callback = f else f(r)
        }
    }

    data class Result<T>(val result: T?, val error: Throwable? = null)

    companion object {

        fun <T> just(value: T): CompletableFuture<T> {
            return CompletableFuture<T>().apply { result = Result(value) }
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