package y2k.joyreactor

import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UIApplicationDelegateAdapter
import org.robovm.apple.uikit.UIApplicationLaunchOptions
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.http.HttpClient
import y2k.joyreactor.platform.DispatchQueueSchedulerFactory
import y2k.joyreactor.platform.NetworkActivityIndicatorHttpClient
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.platform.PlatformImpl

class Main : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching(application: UIApplication?, launchOptions: UIApplicationLaunchOptions?): Boolean {
        Platform.Instance = PlatformImpl()
        ForegroundScheduler.setInstance(DispatchQueueSchedulerFactory().make())
        HttpClient.instance = NetworkActivityIndicatorHttpClient()
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

