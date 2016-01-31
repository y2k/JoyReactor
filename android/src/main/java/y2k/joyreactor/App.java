package y2k.joyreactor;

import android.app.Application;
import android.provider.MediaStore;
import rx.Observable;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.platform.AndroidNavigation;
import y2k.joyreactor.platform.HandlerSchedulerFactory;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.platform.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by y2k on 9/26/15.
 */
public class App extends Application {

    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        ForegroundScheduler.setInstance(new HandlerSchedulerFactory().make());
        Platform.Companion.setInstance(new Platform() {

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
                    in = getAssets().open(path);
                    byte[] result = new byte[in.available()];
                    in.read(result);
                    return result;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public Observable<?> saveToGallery(File imageFile) {
                return ObservableUtils.create(() ->
                    MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), null, null));
            }
        });
    }
}