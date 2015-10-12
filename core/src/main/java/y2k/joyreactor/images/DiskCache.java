package y2k.joyreactor.images;

import rx.Observable;
import y2k.joyreactor.Platform;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by y2k on 9/27/15.
 */
class DiskCache {

    ReadAction load(String url) {
        if (!urlToFile(url).exists()) return null;
        return new ReadAction() {

            @Override
            public File getPath() {
                return urlToFile(url);
            }

            @Override
            public void close() throws IOException {
                // TODO
            }
        };
    }

    Observable<?> putAsync(File newImageFile, String url) {
        // TODO: оптимизировать
        newImageFile.renameTo(urlToFile(url));
        return Observable.just(null);
    }

    private File urlToFile(String url) {
        return new File(getCacheDirectory(), "" + url.hashCode());
    }

    File getCacheDirectory() {
        return Platform.Instance.getCurrentDirectory();
    }

    interface ReadAction extends Closeable {

        File getPath();
    }
}
