package y2k.joyreactor.platform

import org.robovm.apple.dispatch.DispatchQueue
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 9/26/15.
 */
class DispatchQueueSchedulerFactory {

    fun make(): Scheduler {
        return Schedulers.from { DispatchQueue.getMainQueue().async(it) }
    }
}