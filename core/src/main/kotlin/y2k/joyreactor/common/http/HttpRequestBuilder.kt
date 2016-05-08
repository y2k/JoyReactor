package y2k.joyreactor.common.http

import okhttp3.MediaType
import okhttp3.RequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import y2k.joyreactor.common.stream
import java.net.URLEncoder
import java.util.*

class HttpRequestBuilder(private val httpClient: HttpClient) {

    private val form = HashMap<String, String>()
    private val headers = HashMap<String, String>()

    fun addField(key: String, value: String): HttpRequestBuilder {
        form.put(key, value)
        return this
    }

    fun putHeader(name: String, value: String): HttpRequestBuilder {
        headers.put(name, value)
        return this
    }

    fun get(url: String): Document {
        var response = httpClient.executeRequest(url, true) {
            headers.forEach { header(it.key, it.value) }
        }
        return response.stream().use { Jsoup.parse(it, "utf-8", url) }
    }

    fun post(url: String): Document {
        var response = httpClient.executeRequest(url, true) {
            headers.forEach { header(it.key, it.value) }
            post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), serializeForm()))
        }
        return response.stream().use { Jsoup.parse(it, "utf-8", url) }
    }

    private fun serializeForm(): ByteArray {
        return form
            .map { it.key + "=" + URLEncoder.encode(it.value, "UTF-8") }
            .joinToString (separator = "&")
            .toByteArray()
    }
}