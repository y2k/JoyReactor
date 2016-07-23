package y2k.joyreactor.common

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.delay
import y2k.joyreactor.services.BroadcastService
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 10/07/16.
 */
object BackgroundWorks {

    private val CACHE_LIFE = TimeUnit.SECONDS.toMillis(30)
    private val statusMap = HashMap<String, WorkStatus>()

    fun updateWorkStatus(key: String) {
        markWorkStarted(key)
    }

    fun markWorkStarted(key: String) {
        statusMap[key] = WorkStatus(isFinished = false)
        BroadcastService.broadcast(key)
    }

    fun markWorkFinished(key: String, error: Throwable? = null) {
        if (error == null) {
            statusMap.remove(key)
        } else {
            statusMap[key] = WorkStatus(isFinished = true, error = error)
            async_ {
                await(delay(CACHE_LIFE))
                statusMap.remove(key)
            }
        }
        BroadcastService.broadcast(key)
    }

    fun getStatus(key: String): WorkStatus {
        return statusMap[key] ?: WorkStatus(isFinished = true)
    }
}