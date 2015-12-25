package y2k.joyreactor.images

import rx.Observable
import rx.Subscription
import y2k.joyreactor.Image
import y2k.joyreactor.common.ForegroundScheduler
import java.io.File
import java.util.*

/**
 * Created by y2k on 12/10/15.
 */
abstract class BaseImageRequest<T> {

    private var subscription: Subscription? = null

    private var image: Image? = null
    private var width: Int = 0
    private var height: Int = 0

    open fun setSize(width: Int, height: Int): BaseImageRequest<T> {
        this.width = width
        this.height = height
        return this
    }

    fun setUrl(image: Image?): BaseImageRequest<T> {
        this.image = image
        return this
    }

    fun to(target: Any, callback: (T?) -> Unit) {
        if (image == null) {
            sLinks.remove(target)
            callback(null)
            return
        }

        subscription = fromCache
                .flatMap({ image ->
                    if (image != null) Observable.just<T>(image)
                    else putToCache()
                            .flatMap { s -> fromCache }
                            .map { it!! }
                })
                .observeOn(ForegroundScheduler.getInstance())
                .filter { s -> sLinks[target] === subscription }
                .subscribe(
                        { callback(it) },
                        { it.printStackTrace() },
                        { sLinks.remove(target) })

        callback(null)
        sLinks.put(target, subscription!!)
    }

    private val fromCache: Observable<T?>
        get() = sDiskCache[toURLString()].map({ it?.let { decode(it) } })

    private fun putToCache(): Observable<Any> {
        val dir = sDiskCache.cacheDirectory
        return MultiTryDownloader(dir, toURLString())
                .downloadAsync()
                .flatMap({ s -> sDiskCache.put(s, toURLString()) })
    }

    private fun toURLString(): String {
        return image!!.thumbnailUrl(width, height)
    }

    protected abstract fun decode(path: File): T

    companion object {

        private val sDiskCache = DiskCache()
        private val sLinks = HashMap<Any, Subscription>()
    }
}