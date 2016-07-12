package y2k.joyreactor.common

import rx.Completable
import y2k.joyreactor.services.BroadcastService
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 10/07/16.
 */
object WorkQueue {

    private val CACHE_LIFE = 1L to TimeUnit.SECONDS
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

    var inProgress: Boolean = true
    var finishedWithError: Boolean = false

    init {
        completable
            .ui({
                inProgress = false
                BroadcastService.broadcast(this)

                onFinished(null)
            }, {
                it.printStackTrace()
                inProgress = false
                finishedWithError = true
                BroadcastService.broadcast(this)

                onFinished(it)
            })
    }
}