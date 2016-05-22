package y2k.joyreactor.common

import android.support.design.widget.BottomSheetDialogFragment
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 5/22/2016.
 */
open class LifeCycleBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val lifeCycle = LifeCycleService(ServiceLocator.resolve<BroadcastService>())

    override fun onResume() {
        super.onResume()
        lifeCycle.activate()
    }

    override fun onPause() {
        super.onPause()
        lifeCycle.deactivate()
    }
}