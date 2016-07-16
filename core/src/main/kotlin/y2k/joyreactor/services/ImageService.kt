package y2k.joyreactor.services

import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.images.DiskCache
import java.util.*

/**
 * Created by y2k on 5/15/16.
 */
class ImageService(
    private val diskCache: DiskCache,
    private val client: HttpClient,
    private val decoder: Platform,
    private val metaHolder: MetaStorage) {

    fun makeUrl(image: Image?, width: Int, height: Int): String? {
        return image?.thumbnailUrl(width, height)
    }

    fun <T> to(imageUrl: String?, target: Any): CompletableContinuation<T?> {
        if (imageUrl == null) {
            metaHolder.setKey(target, null)
            return CompletableContinuation.just(null)
        }

        val id = UUID.randomUUID()
        metaHolder.setKey(target, id)

        return getFromCache<T>(imageUrl)
            .switchIfNull { replaceToCache(imageUrl) }
            .startWith(null as T?)
            .filter { metaHolder.getKey(target) == id }
    }

    private fun <T> replaceToCache(url: String): CompletableContinuation<T> {
        val tmp = createTempFile(directory = diskCache.cacheDirectory)
        return client.downloadToFile(url, tmp)
            .thenAsync { diskCache.put(tmp, url) }
            .thenAsync { getFromCache<T>(url).then { it!! } }
    }

    private fun <T> getFromCache(url: String): CompletableContinuation<T?> {
        return diskCache
            .get(url)
            .thenAsync { decoder.decodeImage<T>(it) }
    }

    interface MetaStorage {

        fun setKey(target: Any, key: UUID?)
        fun getKey(target: Any): UUID?
    }
}
