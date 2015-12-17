package y2k.joyreactor.images;

import rx.Observable;
import rx.schedulers.Schedulers;
import y2k.joyreactor.common.Optional;
import y2k.joyreactor.platform.Platform;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by y2k on 9/27/15.
 */
class DiskCache {

    private static Executor DISK_EXECUTOR = Executors.newSingleThreadExecutor();

    public DiskCache() {
        getCacheDirectory().mkdirs();
    }

    Observable<Optional<File>> loadOptionalAsync(String url) {
        return Observable.<Optional<File>>create(subscriber -> {
            File file = urlToFile(url);
            subscriber.onNext(file.exists() ? Optional.of(file) : Optional.<File>empty());
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(DISK_EXECUTOR));
    }

    Observable<?> putAsync(File newImageFile, String url) {
        return Observable.create(subscriber -> {
            newImageFile.renameTo(urlToFile(url));
            subscriber.onNext(null);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(DISK_EXECUTOR));
    }

    private File urlToFile(String url) {
        return new File(getCacheDirectory(), "" + url.hashCode());
    }

    File getCacheDirectory() {
        return new File(Platform.Instance.getCurrentDirectory(), "images");
    }
}