package y2k.joyreactor.common.http

import org.jsoup.nodes.Document
import y2k.joyreactor.common.async.CompletableFuture
import java.io.File

/**
 * Created by y2k on 5/8/16.
 */
interface HttpClient {

    fun downloadToFile(url: String, file: File): CompletableFuture<*>

    fun downloadToFile(url: String, file: File, callback: ((Int, Int) -> Unit)?)

    fun getText(url: String): String

    fun getDocument(url: String): Document

    fun clearCookies()

    fun buildRequest(): RequestBuilder
}

interface RequestBuilder {

    fun addField(key: String, value: String): RequestBuilder

    fun putHeader(name: String, value: String): RequestBuilder

    fun get(url: String): Document

    fun post(url: String): Document
}