package y2k.joyreactor.common.http

import okhttp3.MediaType
import okhttp3.RequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import y2k.joyreactor.common.stream
import java.net.URLEncoder
import java.util.*

class HttpRequestBuilder(private val httpClient: DefaultHttpClient) : RequestBuilder {

    private val form = HashMap<String, String>()
    private val headers = HashMap<String, String>()

    override fun addField(key: String, value: String): HttpRequestBuilder {
        form.put(key, value)
        return this
    }

    override fun putHeader(name: String, value: String): HttpRequestBuilder {
        headers.put(name, value)
        return this
    }

    override fun get(url: String): Document {
        val response = httpClient.executeRequest(url, true) {
            headers.forEach { header(it.key, it.value) }
        }
        return response.stream().use { Jsoup.parse(it, "utf-8", url) }
    }

    override fun post(url: String): Document {
        val response = httpClient.executeRequest(url, true) {
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