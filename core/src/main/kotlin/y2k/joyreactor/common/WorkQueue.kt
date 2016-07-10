package y2k.joyreactor.common

import rx.Completable
import y2k.joyreactor.services.BroadcastService
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 10/07/16.
 */
object WorkQueue {

    private val CACHE_LIFE = 30L to TimeUnit.SECONDS
    private val workMap = HashMap<Any, LongWorkTask>()

    fun add(key: Any, completable: Completable): LongWorkTask {
        val cached = workMap[key]
        if (cached != null) return cached

        var task: LongWorkTask? = null
        task = LongWorkTask(completable) {
            if (it != null) workMap.remove(key)
            else {
                ForegroundScheduler.instance.createWorker().schedule({
                    if (workMap[key] === task) workMap.remove(key)
                }, CACHE_LIFE.first, CACHE_LIFE.second)
            }
        }

        workMap[key] = task
        return task
    }
}

fun Completable.toTask(key: Any): LongWorkTask = WorkQueue.add(key, this)

class LongWorkTask(
    completable: Completable, private val onFinished: (Throwable?) -> Unit) {

    val notification = Notifications.PostSync

    @Volatile var isBusy: Boolean = true
    @Volatile var finishedWithError: Boolean = false

    init {
        completable
            .doOnCompleted {
                isBusy = false
                BroadcastService.broadcast(notification)
            }
            .doOnError {
                it.printStackTrace()
                isBusy = false
                finishedWithError = true
                BroadcastService.broadcast(notification)
            }
            .ui({ onFinished(null) }, onFinished)
    }
}