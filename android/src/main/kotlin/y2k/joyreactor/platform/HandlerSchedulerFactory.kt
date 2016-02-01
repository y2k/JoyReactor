package y2k.joyreactor.platform

import android.os.Handler
import android.os.Looper
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 9/26/15.
 */
class HandlerSchedulerFactory {

    fun make(): Scheduler = Schedulers.from { mainHandler.post(it) }

    companion object {

        private val mainHandler = Handler(Looper.getMainLooper())
    }
}