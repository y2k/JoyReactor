package y2k.joyreactor.common

import okhttp3.Response
import y2k.joyreactor.common.http.HttpRequestBuilder
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Created by y2k on 4/26/16.
 */

fun HttpRequestBuilder.ajax(referer: String): HttpRequestBuilder {
    putHeader("X-Requested-With", "XMLHttpRequest")
    putHeader("Referer", referer)
    return this;
}

fun Response.stream(): InputStream {
    return if ("gzip" == header("Content-Encoding")) GZIPInputStream(body().byteStream()) else body().byteStream()
}