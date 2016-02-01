package y2k.joyreactor.common

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by y2k on 2/1/16.
 */
abstract class ActivityLifecycleCallbacksAdapter : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity?) {
        // STUB
    }

    override fun onActivityStarted(activity: Activity?) {
        // STUB
    }

    override fun onActivityDestroyed(activity: Activity?) {
        // STUB
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // STUB
    }

    override fun onActivityStopped(activity: Activity?) {
        // STUB
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        // STUB
    }

    override fun onActivityResumed(activity: Activity?) {
        // STUB
    }
}