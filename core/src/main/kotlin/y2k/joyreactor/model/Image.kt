package y2k.joyreactor.model

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

    val mp4: String
        get() = fullUrl("mp4")

    private val url: String? = clearUrl(url)

    val original: String
        get() = fullUrl(if (isAnimated) "mp4" else null)

    fun fullUrl(format: String? = null): String {
        try {
            return toURL(null, null, format).toString()
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
    }

    fun thumbnailUrl(width: Int?, height: Int?): String = toURL(width, height, null).toString()

    private fun toURL(width: Int?, height: Int?, format: String?): URL {
        val base = URL("https://rc.y2k.work/")
        if (width == null || height == null)
            return URL(base, "/cache/original?url=$url${getFormatPart(format)}")
        return URL(base, "/cache/fit?quality=30&bgColor=ffffff&width=$width&height=$height&url=$url")
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

    override fun equals(other: Any?): Boolean {
        return (other is Image) && other.url == url;
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