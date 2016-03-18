package y2k.joyreactor.http

import okhttp3.Request
import okhttp3.Response
import y2k.joyreactor.common.PersistentMap
import java.net.HttpURLConnection
import java.util.regex.Pattern

/**
 * Created by y2k on 10/11/15.
 */
class CookieStorage {

    private val map = PersistentMap("cookies.1.dat")

    fun attach(request: Request.Builder) {
        if (map.isEmpty) return

        val cookie = StringBuilder()
        for (key in map.keySet())
            cookie.append(key).append("=").append(map[key]).append("; ")
        request.header("Cookie", cookie.toString())
    }

    fun attach(connection: HttpURLConnection) {
        if (map.isEmpty) return

        val cookie = StringBuilder()
        for (key in map.keySet())
            cookie.append(key).append("=").append(map[key]).append("; ")
        connection.addRequestProperty("Cookie", cookie.toString())
    }

    fun grab(response: Response) {
        val cookies = response.headers("Set-Cookie")
        if (cookies == null || cookies.isEmpty()) return
        for (c in cookies) {
            val m = COOKIE_PATTERN.matcher(c)
            if (!m.find()) throw IllegalStateException(c)
            map.put(m.group(1), m.group(2))
        }
        map.flush()
    }

    fun grab(connection: HttpURLConnection) {
        val cookies = connection.headerFields["Set-Cookie"]
        if (cookies == null || cookies.isEmpty()) return
        for (c in cookies) {
            val m = COOKIE_PATTERN.matcher(c)
            if (!m.find()) throw IllegalStateException(c)
            map.put(m.group(1), m.group(2))
        }
        map.flush()
    }

    fun clear() {
        map.clear()
    }

    companion object {

        private val COOKIE_PATTERN = Pattern.compile("(.+?)=([^;]+)")
    }
}