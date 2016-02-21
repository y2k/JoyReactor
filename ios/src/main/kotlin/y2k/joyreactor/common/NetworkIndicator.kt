package y2k.joyreactor.common

import org.robovm.apple.uikit.UIApplication
import rx.Observable
import rx.Subscriber
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 10/13/15.
 */
class NetworkIndicator {

    private lateinit var subscriber: Subscriber<in Boolean>

    init {
        Observable
            .create<Boolean> { subscriber -> this.subscriber = subscriber }
            .buffer(500, TimeUnit.MILLISECONDS)
            .filter { it.size > 0 }
            .map { it[it.size - 1] }
            .observeOn(ForegroundScheduler.instance)
            .subscribe { s -> update(s as Boolean) }
    }

    fun setEnabled(isEnabled: Boolean) {
        subscriber.onNext(isEnabled)
    }

    private fun update(isEnabled: Boolean) {
        UIApplication.getSharedApplication().isNetworkActivityIndicatorVisible = isEnabled
    }
}