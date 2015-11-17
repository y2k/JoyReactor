package y2k.joyreactor;

import android.app.Application;
import android.content.res.AssetManager;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.common.IoUtils;
import y2k.joyreactor.platform.AndroidNavigation;
import y2k.joyreactor.platform.HandlerScheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by y2k on 9/26/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ForegroundScheduler.setInstance(new HandlerScheduler());
        Platform.Instance = new Platform() {

            AndroidNavigation navigation = new AndroidNavigation(App.this);

            @Override
            public File getCurrentDirectory() {
                return getFilesDir();
            }

            @Override
            public Navigation getNavigator() {
                return navigation;
            }

            @Override
            public byte[] loadFromBundle(String name, String ext) {
                String path = name + "." + ext;
                InputStream in = null;
                try {
                    byte[] result = new byte[(int) getAssets().openFd(path).getLength()];
                    in = getAssets().open(path);
                    in.read(result);
                    return result;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    IoUtils.close(in);
                }
            }
        };
    }
}