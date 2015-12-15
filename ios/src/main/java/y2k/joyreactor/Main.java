package y2k.joyreactor;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.http.HttpClient;
import y2k.joyreactor.platform.DispatchQueueSchedulerFactory;
import y2k.joyreactor.platform.NetworkActivityIndicatorHttpClient;
import y2k.joyreactor.platform.Platform;
import y2k.joyreactor.platform.PlatformImpl;

public class Main extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        Platform.Instance = new PlatformImpl();
        ForegroundScheduler.setInstance(new DispatchQueueSchedulerFactory().make());
        HttpClient.setInstance(new NetworkActivityIndicatorHttpClient());
        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, Main.class);
        }
    }
}