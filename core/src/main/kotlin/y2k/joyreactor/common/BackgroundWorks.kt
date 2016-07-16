package y2k.joyreactor.common

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.delay
import y2k.joyreactor.services.BroadcastService
import java.util.*

/**
 * Created by y2k on 10/07/16.
 */
object BackgroundWorks {

    private const val CACHE_LIFE = 30 * 1000L
    private val statusMap = HashMap<Any, WorkStatus>()

    fun updateWorkStatus(key: Any) {
        markWorkStarted(key)
    }

    fun markWorkStarted(key: Any) {
        statusMap[key] = WorkStatus(isFinished = false)
        BroadcastService.broadcast(key)
    }

    fun markWorkFinished(key: Any, error: Throwable? = null) {
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

    fun getStatus(key: Any): WorkStatus {
        return statusMap[key] ?: WorkStatus(isFinished = true)
    }
}