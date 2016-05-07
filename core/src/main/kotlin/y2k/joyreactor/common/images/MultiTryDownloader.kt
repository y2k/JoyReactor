package y2k.joyreactor.common.images

import rx.Single
import rx.SingleSubscriber
import y2k.joyreactor.common.schedule
import y2k.joyreactor.common.http.HttpClient
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 12/10/15.
 */
internal class MultiTryDownloader(private val httpClient: HttpClient) {

    fun downloadAsync(tempDir: File, url: String): Single<File> {
        return Single.create<File> { downloadAsync(tempDir, url, 0, it) }
    }

    private fun downloadAsync(tempDir: File, url: String, tryNumber: Int, subscriber: SingleSubscriber<in File>) {
        TIMER.schedule(250L shl tryNumber) {
            DOWNLOAD_EXECUTOR.execute {
                try {
                    subscriber.onSuccess(downloadToTempFile(tempDir, url, subscriber))
                } catch (e: Exception) {
                    if (tryNumber >= MAX_RETRY || subscriber.isUnsubscribed) subscriber.onError(e)
                    else downloadAsync(tempDir, url, tryNumber + 1, subscriber)
                }
            }
        }
    }

    private fun downloadToTempFile(tempDir: File, url: String, subscriber: SingleSubscriber<in File>): File {
        var result: File? = null
        try {
            result = File.createTempFile("download_", null, tempDir)
            httpClient.downloadToFile(url, result) { a, b ->
                if (subscriber.isUnsubscribed) throw IOException("DOWNLOAD CANCELED")
            }
            return result
        } catch (e: Exception) {
            result?.delete()
            throw e
        }
    }

    companion object {

        private val MAX_RETRY = 5
        private val MAX_THREADS = 5
        private val TIMER = Executors.newSingleThreadScheduledExecutor()
        private val DOWNLOAD_EXECUTOR = ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 1, TimeUnit.SECONDS,
            object : LinkedBlockingDeque<Runnable>() {

                override fun offer(e: Runnable?): Boolean {
                    return super.offerFirst(e)
                }
            }).apply { allowCoreThreadTimeOut(true) }
    }
}