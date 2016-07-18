package y2k.joyreactor.services.requests

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.platform.Platform
import java.io.File
import java.util.regex.Pattern

/**
 * Created by y2k on 16/10/15.
 */
class OriginalImageRequestFactory(
    private val httpClient: HttpClient,
    private val platform: Platform) :
    Function2<String, Boolean, CompletableFuture<File?>> {

    override operator fun invoke(imageUrl: String, onlyFromCache: Boolean): CompletableFuture<File?> {
        if (onlyFromCache) return requestFromCache(imageUrl)

        return runAsync {
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

    private fun requestFromCache(imageUrl: String): CompletableFuture<File?> {
        return runAsync {
            val file = getTargetFile(imageUrl)
            if (!file.exists()) throw Exception()
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