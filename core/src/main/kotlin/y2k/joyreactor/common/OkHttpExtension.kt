package y2k.joyreactor.common

import okhttp3.Response
import okio.GzipSource
import okio.Okio

/**
 * Created by y2k on 5/2/16.
 */

fun Response.string(): String {
    // TODO: заменить на стандартный метод string(), когда он будет поддержвать Gzip
    return when {
        isCompressed() -> Okio.buffer(GzipSource(body().source())).readUtf8()
        else -> body().string()
    }
}

private fun Response.isCompressed() = "gzip".equals(header("Content-Encoding"), true)