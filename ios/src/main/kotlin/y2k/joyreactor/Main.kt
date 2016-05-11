package y2k.joyreactor

import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UIApplicationDelegateAdapter
import org.robovm.apple.uikit.UIApplicationLaunchOptions
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.platform.DispatchQueueSchedulerFactory
import y2k.joyreactor.platform.NetworkActivityIndicatorHttpClient

class Main : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching(application: UIApplication?, launchOptions: UIApplicationLaunchOptions?): Boolean {
        ForegroundScheduler.instance = DispatchQueueSchedulerFactory().make()
        var client = NetworkActivityIndicatorHttpClient(ServiceLocator.resolve<HttpClient>())
        ServiceLocator.registerSingleton { client }
        return true
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val pool = NSAutoreleasePool()
            UIApplication.main<UIApplication, Main>(args, null, Main::class.java)
            pool.release()
        }
    }
}