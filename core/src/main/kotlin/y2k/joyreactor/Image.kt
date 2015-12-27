package y2k.joyreactor

import java.io.Serializable
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by y2k on 10/29/15.
 */
class Image(
        url: String? = null,
        private val width: Int = 0,
        private val height: Int = 0) : Serializable {

    private val url: String? = clearUrl(url)

    fun fullUrl(format: String?): String {
        try {
            return toURL(0, 0, format).toString()
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
    }

    fun thumbnailUrl(width: Int, height: Int): String {
        try {
            return toURL(width, height, null).toString()
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
    }

    private fun toURL(width: Int, height: Int, format: String?): URL {
        if (width == 0 || height == 0)
                return URL("https", "api-i-twister.net", 8011, "/cache/original?url=" + url + getFormatPart(format))

        return URL(
                "https", "api-i-twister.net", 8011,
                "/cache/fit?quality=30&bgColor=ffffff&width=$width&height=$height&url=$url")
    }

    private fun getFormatPart(format: String?): String {
        return if (format == null) "" else "&format=" + format
    }

    val isAnimated: Boolean
        get() = url != null && url.endsWith(".gif")

    val aspect: Float
        get() = getAspect(0f)

    fun getAspect(min: Float): Float {
        val aspect = if (height == 0) 1f else width.toFloat() / height
        return Math.min(2f, Math.max(min, aspect))
    }

    override fun equals(obj: Any?): Boolean {
        return (obj is Image) && obj.url == url;
    }

    companion object {

        private val serialVersionUID = 0

        private fun clearUrl(url: String?): String? {
            return url
                    ?.replace("(/comment/).+(-\\d+\\.[\\w\\d]+)$".toRegex(), "$1$2")
                    ?.replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
                    ?.replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2")
        }
    }
}