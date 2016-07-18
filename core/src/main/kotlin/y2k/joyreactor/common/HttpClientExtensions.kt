package y2k.joyreactor.common

import org.jsoup.nodes.Document
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.RequestBuilder

/**
 * Created by y2k on 4/26/16.
 */

fun RequestBuilder.ajax(referer: String): RequestBuilder {
    putHeader("X-Requested-With", "XMLHttpRequest")
    putHeader("Referer", referer)
    return this;
}

fun RequestBuilder.postAsync(url: String): CompletableFuture<Document> {
    return runAsync { post(url) }
}

fun HttpClient.getDocumentAsync(url: String): CompletableFuture<Document> {
    return runAsync { getDocument(url) }
}

fun RequestBuilder.getAsync(url: String): CompletableFuture<Document> {
    return runAsync { get(url) }
}

fun HttpClient.getTextAsync(url: String): CompletableFuture<String> {
    return runAsync { getText(url) }
}