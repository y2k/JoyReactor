package y2k.joyreactor.http

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rx.Observable
import y2k.joyreactor.common.ioObservable
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Created by y2k on 9/29/15.
 */
open class HttpClient protected constructor() {

    open fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?) {
        val connection = URL(url).openConnection()
        connection.inputStream.use { inStream ->
            file.outputStream().use { outStream ->
                val buf = ByteArray(4 * 1024)
                var count: Int
                var transfer = 0
                var lastCallTime: Long = 0

                while (true) {
                    count = inStream!!.read(buf)
                    if (count == -1) break;

                    outStream.write(buf, 0, count)

                    if (callback != null) {
                        transfer += count
                        if (System.currentTimeMillis() - lastCallTime > 1000 / 60) {
                            callback(transfer, connection.contentLength)
                            lastCallTime = System.currentTimeMillis()
                        }
                    }
                }
            }
        }
    }

    open fun getText(url: String): String {
        var stream: InputStream? = null
        try {
            val conn = createConnection(url)
            stream = getInputStream(conn)
            sCookies.grab(conn)

            val reader = BufferedReader(InputStreamReader(stream))
            val buffer = StringBuilder()
            var line: String

            while (true) {
                line = reader.readLine()
                if (line == null) break;
                buffer.append(line).append("\n")
            }
            return buffer.toString()
        } finally {
            if (stream != null) stream.close()
        }
    }

    fun getDocumentAsync(url: String): Observable<Document> {
        return ioObservable { getDocument(url) }
    }

    open fun getDocument(url: String): Document {
        var stream: InputStream? = null
        try {
            val conn = createConnection(url)
            stream = getInputStream(conn)
            sCookies.grab(conn)
            return Jsoup.parse(stream, "utf-8", url)
        } finally {
            if (stream != null) stream.close()
        }
    }

    private fun getInputStream(connection: HttpURLConnection): InputStream {
        var connection = connection
        val redirect = connection.getHeaderField("Location")
        if (redirect != null) {
            connection.disconnect()
            connection = createConnection(redirect)
        }

        val stream = if (connection.responseCode < 300) connection.inputStream else connection.errorStream
        return if ("gzip" == connection.contentEncoding) GZIPInputStream(stream) else stream
    }

    private fun createConnection(url: String): HttpURLConnection {
        val conn: HttpURLConnection
        conn = URL(url).openConnection() as HttpURLConnection
        conn.setRequestProperty("Accept-Encoding", "gzip")
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko")
        sCookies.attach(conn)
        return conn
    }

    fun beginForm(): Form {
        return Form()
    }

    fun clearCookies() {
        sCookies.clear()
    }

    inner class Form {

        internal var form: MutableMap<String, String> = HashMap()
        internal var headers: MutableMap<String, String> = HashMap()

        fun put(key: String, value: String): Form {
            form.put(key, value)
            return this
        }

        fun putHeader(name: String, value: String): Form {
            headers.put(name, value)
            return this
        }

        fun send(url: String): Document {
            val connection = createConnection(url)
            connection.requestMethod = "POST"
            connection.instanceFollowRedirects = false
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            for (name in headers.keys)
                connection.addRequestProperty(name, headers[name])
            connection.outputStream.write(serializeForm())

            // TODO:
            var stream: InputStream? = null
            try {
                stream = getInputStream(connection)
                sCookies.grab(connection)
                return Jsoup.parse(stream, "utf-8", url)
            } finally {
                if (stream != null) stream.close()
            }
        }

        private fun serializeForm(): ByteArray {
            val buffer = StringBuilder()
            for (key in form.keys) {
                buffer.append(key)
                buffer.append("=")
                buffer.append(URLEncoder.encode(form[key], "UTF-8"))
                buffer.append("&")
            }
            buffer.replace(buffer.length - 1, buffer.length, "")
            return buffer.toString().toByteArray()
        }
    }

    companion object {

        var instance = HttpClient()

        private val sCookies = CookieStorage()
    }
}