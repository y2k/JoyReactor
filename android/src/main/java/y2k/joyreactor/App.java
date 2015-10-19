package y2k.joyreactor;

import android.app.Application;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.platform.HandlerScheduler;

/**
 * Created by y2k on 9/26/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ForegroundScheduler.setInstance(new HandlerScheduler());
    }
}