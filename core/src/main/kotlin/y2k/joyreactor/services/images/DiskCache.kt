package y2k.joyreactor.services.images

import y2k.joyreactor.common.ApplicationDataVersion
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.platform.Platform
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by y2k on 9/27/15.
 */
class DiskCache(private val platform: Platform) {

    init {
        cacheDirectory.mkdirs()
    }

    fun get(url: String): CompletableFuture<File?> {
        return runAsync(DISK_EXECUTOR) {
            urlToFile(url).let { if (it.exists()) it else null }
        }
    }

    fun put(newImageFile: File, url: String): CompletableFuture<*> {
        return runAsync(DISK_EXECUTOR) {
            newImageFile.renameTo(urlToFile(url))
        }
    }

    private fun urlToFile(url: String) = File(cacheDirectory, "" + url.hashCode())

    val cacheDirectory: File
        get() = File(platform.currentDirectory, "images.$ApplicationDataVersion")

    companion object {

        private val DISK_EXECUTOR = Executors.newSingleThreadExecutor()
    }
}