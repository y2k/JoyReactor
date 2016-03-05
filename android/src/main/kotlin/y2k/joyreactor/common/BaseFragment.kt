package y2k.joyreactor.common

import android.support.v4.app.Fragment
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 2/3/16.
 */
open class BaseFragment : Fragment() {

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