package y2k.joyreactor.services

import y2k.joyreactor.common.async.CanceledException
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.async
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

    fun <T> download(imageUrl: String?, target: Any): CompletableContinuation<T?> {
        return async {
            if (imageUrl == null) {
                metaHolder.setKey(target, null)
                return@async null
            }

            val id = UUID.randomUUID()
            metaHolder.setKey(target, id)

            val fromCache = await(getFromCache<T>(imageUrl))
            val result = if (fromCache != null) fromCache
            else {
                await(replaceToCache(imageUrl))
                await(getFromCache<T>(imageUrl))
            }

            if (metaHolder.getKey(target) != id) throw CanceledException()
            result
        }
    }

    private fun replaceToCache(url: String): CompletableContinuation<*> {
        val tmp = createTempFile(directory = diskCache.cacheDirectory)
        return client
            .downloadToFile(url, tmp)
            .thenAsync { diskCache.put(tmp, url) }
    }

    private fun <T> getFromCache(url: String): CompletableContinuation<T?> {
        return diskCache
            .get(url)
            .thenAsync {
                if (it == null) CompletableContinuation.just(null)
                else decoder.decodeImageAsync<T>(it)
            }
    }

    interface MetaStorage {

        fun setKey(target: Any, key: UUID?)
        fun getKey(target: Any): UUID?
    }
}
