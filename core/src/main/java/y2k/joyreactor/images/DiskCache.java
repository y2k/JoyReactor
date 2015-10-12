package y2k.joyreactor.images;

import rx.Observable;
import y2k.joyreactor.Platform;

import java.io.File;

/**
 * Created by y2k on 9/27/15.
 */
class DiskCache {

    private static TaskExecutor DISK_EXECUTOR = new TaskExecutor(1);

    Observable<File> loadAsync(String url) {
        return Observable.create(subscriber -> DISK_EXECUTOR.execute(() -> {
            if (urlToFile(url).exists())
                subscriber.onNext(urlToFile(url));
            subscriber.onCompleted();
        }));
    }

    Observable<?> putAsync(File newImageFile, String url) {
        return Observable.create(subscriber -> DISK_EXECUTOR.execute(() -> {
            newImageFile.renameTo(urlToFile(url));
            subscriber.onCompleted();
        }));
    }

    private File urlToFile(String url) {
        return new File(getCacheDirectory(), "" + url.hashCode());
    }

    File getCacheDirectory() {
        return Platform.Instance.getCurrentDirectory();
    }
}