package y2k.joyreactor.common.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rx.Completable
import y2k.joyreactor.common.ioCompletable
import y2k.joyreactor.common.stream
import y2k.joyreactor.common.string
import java.io.File
import java.io.IOException

/**
 * Created by y2k on 9/29/15.
 */
class DefaultHttpClient(private val cookies: CookieStorage) : HttpClient {

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor {
            val request = it.request().newBuilder()
            cookies.attach(request)
            it.proceed(request.build())
        }
        .addNetworkInterceptor { cookies.grab(it.proceed(it.request())) }
        .build()

    override fun downloadToFile(url: String, file: File): Completable {
        return ioCompletable { downloadToFile(url, file, null) }
    }

    override fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?) {
        val response = executeRequest(url)
        val contentLength = response.body().contentLength().toInt()
        response.body().byteStream().use { inStream ->
            file.outputStream().use { outStream ->
                val buf = ByteArray(4 * 1024)
                var count: Int
                var transfer = 0
                var lastCallTime: Long = 0

                while (true) {
                    count = inStream.read(buf)
                    if (count == -1) break;

                    outStream.write(buf, 0, count)

                    if (callback != null) {
                        transfer += count
                        if (System.currentTimeMillis() - lastCallTime > 1000 / 60) {
                            callback(transfer, contentLength)
                            lastCallTime = System.currentTimeMillis()
                        }
                    }
                }
            }
        }
    }

    override fun getText(url: String): String {
        return executeRequest(url).string()
    }

    override fun getDocument(url: String): Document {
        return executeRequest(url).stream().use { Jsoup.parse(it, "utf-8", url) }
    }

    fun executeRequest(url: String, init: (Request.Builder.() -> Unit)? = null): Response {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16")
            .header("Accept-Encoding", "gzip")
        if (init != null) request.init()

        val response = client.newCall(request.build()).execute()
        if (response.code() >= 400 && response.code() != 401) {
            response.body().close()
            throw IOException("Unexpected code " + response)
        }
        return response
    }

    override fun clearCookies() {
        cookies.clear()
    }

    override fun buildRequest(): RequestBuilder {
        return HttpRequestBuilder(this)
    }
}