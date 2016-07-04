package y2k.joyreactor.services

import rx.Observable
import rx.Subscription
import rx.subjects.BehaviorSubject
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.mapNotNull
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.common.switchIfNull
import y2k.joyreactor.common.ui
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.images.DiskCache
import java.util.*

/**
 * Created by y2k on 5/15/16.
 */
class ImageService(
    private val diskCache: DiskCache,
    private val client: HttpClient,
    private val decoder: Platform) {

    fun makeUrl(image: Image?, width: Int, height: Int): String? {
        return image?.thumbnailUrl(width, height)
    }

    fun <T> to(state: LinksPool, imageUrl: String?, target: Any): Observable<T> {
        state.sLinks.remove(target)?.let { it.unsubscribe() }
        if (imageUrl == null) return Observable.just(null)

        val publish = BehaviorSubject.create<T>()
        var subscription: Subscription? = null
        subscription = getFromCache<T>(imageUrl)
            .switchIfNull { replaceToCache(imageUrl) }
            .ui({
                if (state.sLinks[target] === subscription) {
                    publish.onNext(it)
                    state.sLinks.remove(target)
                }

                publish.onCompleted()
            }, {
                it.printStackTrace()
                if (state.sLinks[target] === subscription)
                    state.sLinks.remove(target)

                publish.onCompleted()
            })

        publish.onNext(null)
        state.sLinks.put(target, subscription)
        return publish
    }

    private fun <T> replaceToCache(url: String): Observable<T> {
        val tmp = createTempFile(directory = diskCache.cacheDirectory)
        return client.downloadToFile(url, tmp)
            .andThen(diskCache.put(tmp, url))
            .andThen(getFromCache<T>(url).map { it!! })
    }

    private fun <T> getFromCache(url: String): Observable<T?> {
        return diskCache.get(url)
            .mapNotNull { decoder.decodeImage<T>(it) }
            .toObservable()
    }
}

class LinksPool {

    val sLinks = HashMap<Any, Subscription>()

    companion object {

        val default = LinksPool()
    }
}