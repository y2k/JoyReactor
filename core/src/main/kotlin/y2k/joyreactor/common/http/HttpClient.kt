package y2k.joyreactor.common.http

import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import java.io.File

/**
 * Created by y2k on 5/8/16.
 */
interface HttpClient {

    fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?)

    fun getText(url: String): String

    fun getDocument(url: String): Document

    fun clearCookies()

    fun buildRequest(): HttpRequestBuilder

    fun executeRequest(url: String, isBrowser: Boolean = false, init: (Request.Builder.() -> Unit)? = null): Response
}