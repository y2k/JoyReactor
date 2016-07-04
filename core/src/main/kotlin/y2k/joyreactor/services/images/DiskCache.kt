package y2k.joyreactor.services.images

import rx.Completable
import rx.Single
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ApplicationDataVersion
import y2k.joyreactor.common.ioCompletable
import y2k.joyreactor.common.ioSingle
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

    fun get(url: String): Single<File?> {
        return ioSingle(Schedulers.from(DISK_EXECUTOR)) {
            urlToFile(url).let { if (it.exists()) it else null }
        }
    }

    fun put(newImageFile: File, url: String): Completable {
        return ioCompletable(Schedulers.from(DISK_EXECUTOR)) {
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