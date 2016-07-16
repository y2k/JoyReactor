package y2k.joyreactor.services.repository

import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.runAsync
import java.util.concurrent.Executors

class Entities(val factory: IDataContext) {

    @Deprecated("")
    fun <T> use(callback: DataContext.() -> T): CompletableContinuation<T> = useAsync(callback)

    @Deprecated("")
    fun <T> useOnce(callback: DataContext.() -> T): CompletableContinuation<T> = useAsync(callback)

    fun <T> useAsync(callback: DataContext.() -> T): CompletableContinuation<T> {
        return runAsync(executor) {
            factory.use { callback(DataContext(it)) }
        }
    }

    companion object {

        private val executor = Executors.newSingleThreadExecutor()
    }
}