package y2k.joyreactor.common

import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.HttpRequestBuilder

/**
 * Created by y2k on 4/26/16.
 */

fun HttpRequestBuilder.ajax(referer: String): HttpRequestBuilder {
    putHeader("X-Requested-With", "XMLHttpRequest")
    putHeader("Referer", referer)
    return this;
}

fun HttpClient.buildRequest(): HttpRequestBuilder {
    return HttpRequestBuilder(this)
}