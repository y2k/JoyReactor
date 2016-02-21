package y2k.joyreactor.common

import org.robovm.apple.uikit.UIViewController
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 2/8/16.
 */
open class BaseUIViewController : UIViewController() {

    val lifeCycleService = LifeCycleService(ServiceLocator.resolve(BroadcastService::class))

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        lifeCycleService.activate()
    }

    override fun viewWillDisappear(p0: Boolean) {
        super.viewWillDisappear(p0)
        lifeCycleService.deactivate()
    }
}