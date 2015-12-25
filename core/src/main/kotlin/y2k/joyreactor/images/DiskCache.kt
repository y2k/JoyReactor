package y2k.joyreactor.images

import rx.Observable
import rx.schedulers.Schedulers
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

    operator fun get(url: String): Observable<File?> {
        return Observable.create<File?> { subscriber ->
            val file = urlToFile(url)
            subscriber.onNext(if (file.exists()) file else null)
            subscriber.onCompleted()
        }.subscribeOn(Schedulers.from(DISK_EXECUTOR))
    }

    fun put(newImageFile: File, url: String): Observable<*> {
        return Observable.create<Any> { subscriber ->
            newImageFile.renameTo(urlToFile(url))
            subscriber.onNext(null)
            subscriber.onCompleted()
        }.subscribeOn(Schedulers.from(DISK_EXECUTOR))
    }

    private fun urlToFile(url: String): File {
        return File(cacheDirectory, "" + url.hashCode())
    }

    val cacheDirectory: File
        get() = File(Platform.Instance.currentDirectory, "images")

    companion object {

        private val DISK_EXECUTOR = Executors.newSingleThreadExecutor()
    }
}