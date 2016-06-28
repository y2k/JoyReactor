package y2k.joyreactor.services.requests

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.platform.Platform
import java.io.File
import java.util.concurrent.CancellationException
import java.util.regex.Pattern

/**
 * Created by y2k on 16/10/15.
 */
class OriginalImageRequestFactory(
    private val httpClient: HttpClient,
    private val platform: Platform) {

    fun requestFromCache(imageUrl: String): Observable<File> {
        return ioObservable {
            val file = getTargetFile(imageUrl)
            if (!file.exists()) throw Exception()
            file
        }
    }

    operator fun invoke(imageUrl: String): Observable<File> {
        return ioObservable {
            val file = getTargetFile(imageUrl)
            if (!file.exists()) {
                try {
                    httpClient.downloadToFile(imageUrl, file, null)
                } catch (e: Exception) {
                    file.delete()
                    throw e
                }
            }
            file
        }
    }

    private fun getTargetFile(imageUrl: String) =
        File(platform.currentDirectory, "${imageUrl.hashCode()}.${getExtension(imageUrl)}")

    private fun getExtension(imageUrl: String): String {
        val fm = Pattern.compile("format=([^&]+)").matcher(imageUrl)
        if (fm.find()) return fm.group(1)

        val m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl)
        if (!m.find()) throw IllegalStateException("can't find extension from url " + imageUrl)
        return m.group(1)
    }
}