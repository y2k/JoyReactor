package y2k.joyreactor.images

import rx.Observable
import rx.Subscriber
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.http.HttpClient
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 12/10/15.
 */
internal class MultiTryDownloader(private val dir: File, private val url: String) {

    fun downloadAsync(): Observable<File> {
        return Observable.create<File> { subscriber -> downloadAsync(0, subscriber) }
    }

    private fun downloadAsync(tryNumber: Int, subscriber: Subscriber<in File>) {
        ForegroundScheduler.getInstance().createWorker().schedule({
            DOWNLOAD_EXECUTOR.execute {
                try {
                    subscriber.onNext(downloadToTempFile())
                    subscriber.onCompleted()
                } catch (e: Exception) {
                    if (tryNumber > MAX_TRY)
                        subscriber.onError(e)
                    else
                        downloadAsync(tryNumber + 1, subscriber)
                }
            }
        }, (250 shl tryNumber).toLong(), TimeUnit.MILLISECONDS)
    }

    @Throws(IOException::class)
    private fun downloadToTempFile(): File {
        var result: File? = null
        try {
            result = File.createTempFile("download_", null, dir)
            HttpClient.getInstance().downloadToFile(url, result, null)
            return result
        } catch (e: IOException) {
            if (result != null) result.delete()
            throw e
        }

    }

    companion object {

        private val DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(3)
        private val MAX_TRY = 5
    }
}