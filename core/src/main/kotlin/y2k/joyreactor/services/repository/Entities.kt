package y2k.joyreactor.services.repository

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.runAsync
import java.util.concurrent.Executors

class Entities(val factory: IDataContext) {

    fun <T> useOnce(callback: DataContext.() -> T) = use(callback).toSingle()

    fun <T> use(callback: DataContext.() -> T): Observable<T> {
        return Observable
            .fromCallable {
                factory.use { callback(DataContext(it)) }
            }
            .subscribeOn(Schedulers.from(executor))
            .observeOn(ForegroundScheduler.instance);
    }

    fun <T> useAsync(callback: DataContext.() -> T): CompletableContinuation<T> {
        return runAsync(executor) {
            factory.use { callback(DataContext(it)) }
        }
    }

    companion object {

        private val executor = Executors.newSingleThreadExecutor()
    }
}