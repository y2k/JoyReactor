package y2k.joyreactor.common

import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 2/26/16.
 */
abstract class LifeCycleActivity : AppCompatActivity() {

    val lifeCycleService = LifeCycleService(ServiceLocator.resolve<BroadcastService>())

    override fun onResume() {
        super.onResume()
        lifeCycleService.activate()
    }

    override fun onPause() {
        super.onPause()
        lifeCycleService.deactivate()
    }
}