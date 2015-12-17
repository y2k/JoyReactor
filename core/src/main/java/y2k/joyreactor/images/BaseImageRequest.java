package y2k.joyreactor.images;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import y2k.joyreactor.Image;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.common.Optional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by y2k on 12/10/15.
 */
public abstract class BaseImageRequest<T> {

    private static DiskCache sDiskCache = new DiskCache();
    private static Map<Object, Subscription> sLinks = new HashMap<>();

    private Subscription subscription;

    private Image image;
    private int width;
    private int height;

    public BaseImageRequest<T> setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public BaseImageRequest<T> setUrl(Image image) {
        this.image = image;
        return this;
    }

    public void to(Object target, Action1<T> callback) {
        if (image == null) {
            sLinks.remove(target);
            callback.call(null);
            return;
        }

        subscription = getFromCache()
                .flatMap(image -> image.isPresent()
                        ? Observable.just(image.get())
                        : putToCache().flatMap(s -> getFromCache()).map(Optional::get))
                .observeOn(ForegroundScheduler.getInstance())
                .filter(s -> sLinks.get(target) == subscription)
                .subscribe(
                        callback::call,
                        Throwable::printStackTrace,
                        () -> sLinks.remove(target));

        callback.call(null);
        sLinks.put(target, subscription);
    }

    private Observable<Optional<T>> getFromCache() {
        return sDiskCache
                .loadOptionalAsync(toURLString())
                .map(optionalFile -> optionalFile.map(this::decode));
    }

    private Observable<Object> putToCache() {
        File dir = sDiskCache.getCacheDirectory();
        return new MultiTryDownloader(dir, toURLString())
                .downloadAsync()
                .flatMap(s -> sDiskCache.putAsync(s, toURLString()));
    }

    private String toURLString() {
        return image.thumbnailUrl(width, height);
    }

    protected abstract T decode(File path);
}