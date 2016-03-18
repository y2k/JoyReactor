package y2k.joyreactor.http

import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URLEncoder
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Created by y2k on 9/29/15.
 */
open class HttpClient() {

    private val client = OkHttpClient()

    open fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?) {
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

    open fun getText(url: String): String {
        return executeRequest(url, true).body().string()
    }

    open fun getDocument(url: String): Document {
        return executeRequest(url, true)
            .gzip().use { Jsoup.parse(it, "utf-8", url) }
    }

    private fun executeRequest(url: String, isBrowser: Boolean = false, init: (Request.Builder.() -> Unit)? = null): Response {
        val request = Request.Builder().url(url)
            .header("Accept-Encoding", "gzip")
        if (isBrowser) {
            request.header("User-Agent", BrowserUserAgent)
            sCookies.attach(request)
        }
        if (init != null) request.init()

        val response = client.newCall(request.build()).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code " + response)

        if (isBrowser) sCookies.grab(response)
        return response
    }

    //    private fun getInputStream(connection: HttpURLConnection): InputStream {
    //        var connection = connection
    //        val redirect = connection.getHeaderField("Location")
    //        if (redirect != null) {
    //            connection.disconnect()
    //            connection = createConnection(redirect)
    //        }
    //
    //        val stream = if (connection.responseCode < 300) connection.inputStream else connection.errorStream
    //        return if ("gzip" == connection.contentEncoding) GZIPInputStream(stream) else stream
    //    }

    fun beginForm(): Form {
        return Form()
    }

    fun clearCookies() {
        sCookies.clear()
    }

    private fun Response.gzip(): InputStream {
        return if ("gzip" == header("Content-Encoding")) GZIPInputStream(body().byteStream()) else body().byteStream()
    }

    inner class Form {

        private val form = HashMap<String, String>()
        private val headers = HashMap<String, String>()

        fun put(key: String, value: String): Form {
            form.put(key, value)
            return this
        }

        fun putHeader(name: String, value: String): Form {
            headers.put(name, value)
            return this
        }

        fun send(url: String): Document {
            var response = executeRequest(url, true) {
                headers.forEach { header(it.key, it.value) }
                post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), serializeForm()))
            }
            return response.gzip().use { Jsoup.parse(it, "utf-8", url) }
        }

        private fun serializeForm(): ByteArray {
            return form
                .map { it.key + "=" + URLEncoder.encode(it.value, "UTF-8") }
                .joinToString (separator = "&")
                .toByteArray()
        }
    }

    companion object {

        private val BrowserUserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko"
        private val sCookies = CookieStorage()
    }
}