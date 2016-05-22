package y2k.joyreactor.common

import org.jsoup.nodes.Document
import rx.Single
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

fun RequestBuilder.postAsync(url: String): Single<Document> {
    return ioObservable { post(url) }.toSingle()
}

fun HttpClient.getDocumentAsync(url: String): Single<Document> {
    return ioObservable { getDocument(url) }.toSingle()
}