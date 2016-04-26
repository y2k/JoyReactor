package y2k.joyreactor.common

import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 4/26/16.
 */

fun HttpClient.Form.ajax(referer: String): HttpClient.Form {
    putHeader("X-Requested-With", "XMLHttpRequest")
    putHeader("Referer", referer)
    return this;
}