package y2k.joyreactor;

import android.app.Application;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.platform.AndroidNavigation;
import y2k.joyreactor.platform.HandlerScheduler;

import java.io.File;

/**
 * Created by y2k on 9/26/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ForegroundScheduler.setInstance(new HandlerScheduler());
        Platform.Instance = new Platform() {

            @Override
            public File getCurrentDirectory() {
                return getFilesDir();
            }

            @Override
            public Navigation getNavigator() {
                return new AndroidNavigation();
            }
        };
    }
}