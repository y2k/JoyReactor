package y2k.joyreactor.common.images

import rx.Single
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.platform.Platform
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by y2k on 9/27/15.
 */
internal class DiskCache {

    init {
        cacheDirectory.mkdirs()
    }

    fun get(url: String): Single<File?> {
        return Single.create<File?> {
            val file = urlToFile(url)
            it.onSuccess(if (file.exists()) file else null)
        }.subscribeOn(Schedulers.from(DISK_EXECUTOR))
    }

    fun put(newImageFile: File, url: String): Single<Unit> {
        return Single.create<Unit> {
            newImageFile.renameTo(urlToFile(url))
            it.onSuccess(null)
        }.subscribeOn(Schedulers.from(DISK_EXECUTOR))
    }

    private fun urlToFile(url: String): File {
        return File(cacheDirectory, "" + url.hashCode())
    }

    val cacheDirectory: File
        get() = File(ServiceLocator.resolve<Platform>().currentDirectory, "images")

    companion object {

        private val DISK_EXECUTOR = Executors.newSingleThreadExecutor()
    }
}