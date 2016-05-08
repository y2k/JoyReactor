package y2k.joyreactor.common

import okhttp3.Response
import okio.GzipSource
import okio.Okio
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Created by y2k on 5/2/16.
 */

fun Response.string(): String {
    // TODO: заменить на стандартный метод string(), когда он будет поддержвать Gzip
    return when {
        isCompressed() -> GzipSource(body().source()).use { Okio.buffer(it).readUtf8() }
        else -> body().string()
    }
}

private fun Response.isCompressed() = "gzip".equals(header("Content-Encoding"), true)

fun Response.stream(): InputStream {
    return if ("gzip" == header("Content-Encoding")) GZIPInputStream(body().byteStream()) else body().byteStream()
}