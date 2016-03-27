package y2k.joyreactor.images

import rx.Single
import rx.Subscription
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.mapNotNull
import y2k.joyreactor.common.subscribe
import y2k.joyreactor.http.HttpClient
import y2k.joyreactor.model.Image
import java.io.File
import java.util.*

/**
 * Created by y2k on 12/10/15.
 */
abstract class BaseImageRequest<T> {

    private lateinit var subscription: Subscription

    private var image: Image? = null
    private var width: Int? = null
    private var height: Int? = null

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

        subscription = getFromCache()
            .flatMap {
                if (it != null) Single.just<T>(it)
                else putToCache().flatMap { getFromCache() }
            }
            .observeOn(ForegroundScheduler.instance)
            .subscribe { result, e ->
                if (sLinks[target] === subscription) {
                    result?.let(callback)
                    e?.printStackTrace()
                    sLinks.remove(target)
                }
            }

        callback(null)
        sLinks.put(target, subscription)
    }

    private fun getFromCache(): Single<T?> {
        return sDiskCache.get(toURLString()).mapNotNull { decode(it) }
    }

    private fun putToCache(): Single<Unit> {
        val dir = sDiskCache.cacheDirectory
        val client = ServiceLocator.resolve<HttpClient>()
        return MultiTryDownloader(client, dir, toURLString())
            .downloadAsync()
            .flatMap { sDiskCache.put(it, toURLString()) }
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