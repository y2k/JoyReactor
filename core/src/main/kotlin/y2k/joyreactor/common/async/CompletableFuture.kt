package y2k.joyreactor.common.async

import java.util.concurrent.*

/**
 * Created by y2k on 16/07/16.
 */
class CompletableFuture<T> {

    private @Volatile var result: Result<T>? = null
    private @Volatile var callback: ((Result<T>) -> Unit)? = null

    fun complete(data: T) {
        synchronized(this) {
            val r = SuccessResult(data)
            result = r
            callback?.invoke(r)
        }
    }

    fun completeExceptionally(exception: Throwable) {
        synchronized(this) {
            val r = FailResult<T>(exception)
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

    fun get(): T {
        val lock = CountDownLatch(1)
        var r: Result<T> = FailResult(Exception())
        thenAccept { r = it; lock.countDown() }
        lock.await()

        if (!r.isSuccess) throw r.error
        return r.result
    }

    class SuccessResult<T>(override val result: T) : Result<T>() {
        override val error: Throwable
            get() = throw UnsupportedOperationException()
        override val isSuccess: Boolean
            get() = true
    }

    class FailResult<T>(override val error: Throwable) : Result<T>() {
        override val isSuccess: Boolean
            get() = false
        override val result: T
            get() = throw UnsupportedOperationException()
    }

    abstract class Result<T>() {
        abstract val error: Throwable
        abstract val result: T
        abstract val isSuccess: Boolean

        val errorOrNull: Throwable?
            get() = if (isSuccess) null else error
    }

    companion object {

        fun <T> completedFuture(value: T): CompletableFuture<T> {
            return CompletableFuture<T>().apply { result = SuccessResult(value) }
        }

        fun <T> failedFuture(e: Throwable): CompletableFuture<T> {
            return CompletableFuture<T>().apply { result = FailResult(e) }
        }
    }
}

private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
private val CORE_POOL_SIZE = CPU_COUNT + 1
private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

private val BACKGROUND_EXECUTOR = ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAXIMUM_POOL_SIZE,
    1, TimeUnit.SECONDS,
    LinkedBlockingQueue<Runnable>(128))

var MAIN_EXECUTOR: Executor = Executor { it.run() }

fun delay(timeSpanInMs: Long): CompletableFuture<*> {
    return runAsync { Thread.sleep(timeSpanInMs) }
}

fun <T> runAsync(f: () -> T): CompletableFuture<T> {
    return runAsync(BACKGROUND_EXECUTOR, f)
}

fun <T> runAsync(executor: Executor, f: () -> T): CompletableFuture<T> {
    val task = CompletableFuture<T>()
    executor.execute {
        try {
            val result = f()
            MAIN_EXECUTOR.execute { task.complete(result) }
        } catch (e: Exception) {
            MAIN_EXECUTOR.execute { task.completeExceptionally(e) }
        }
    }
    return task
}