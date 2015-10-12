package y2k.joyreactor.images;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import y2k.joyreactor.ForegroundScheduler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by y2k on 12/10/15.
 */
public abstract class BaseImageRequest<T> {

    private static DiskCache sDiskCache = new DiskCache();
    private static Map<Object, Subscription> sLinks = new HashMap<>();

    private UrlBuilder urlBuilder = new UrlBuilder();

    public BaseImageRequest<T> setSize(int width, int height) {
        urlBuilder.width = width;
        urlBuilder.height = height;
        return this;
    }

    public BaseImageRequest<T> setUrl(String url) {
        urlBuilder.url = url;
        return this;
    }

    public void to(Object target, Action1<T> callback) {
        Subscription subscription = getFromCache()
                .switchIfEmpty(putToCache().flatMap(s -> getFromCache()))
                .observeOn(ForegroundScheduler.getInstance())
                .subscribe(callback::call, Throwable::printStackTrace, () -> sLinks.remove(target));

        Subscription old = sLinks.put(target, subscription);
        if (old != null) old.unsubscribe();
    }

    private Observable<T> getFromCache() {
        return sDiskCache
                .loadAsync(urlBuilder.buildString())
                .map(this::decode);
    }

    private Observable<Object> putToCache() {
        File dir = sDiskCache.getCacheDirectory();
        return new MultiTryDownloader(dir, urlBuilder.buildString())
                .downloadAsync()
                .flatMap(s -> sDiskCache.putAsync(s, urlBuilder.buildString()));
    }

    protected abstract T decode(File path);
}