package y2k.joyreactor.common

import android.support.v7.app.AppCompatDialogFragment
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 4/25/16.
 */
abstract class BaseDialogFragment : AppCompatDialogFragment() {

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