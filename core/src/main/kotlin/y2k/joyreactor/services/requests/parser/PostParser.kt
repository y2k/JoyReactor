package y2k.joyreactor.services.requests.parser

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.MyLike
import y2k.joyreactor.model.Post
import java.util.*
import java.util.regex.Pattern

/**
 * Created by y2k on 5/29/16.
 */
class PostParser {

    fun parse(document: Element): Post {
        var image = ThumbnailParser(document).load()
        if (image == null) image = YoutubeThumbnailParser(document).load()
        if (image == null) image = VideoThumbnailParser(document).load()

        val parser = PostParser(document)

        val title = document.select("div.post_content > div > h3").first()
        val desc = title?.nextSibling()

        return Post(
            title?.text() ?: "",
            image,
            document.select("div.uhead_nick > img").attr("src"),
            document.select("div.uhead_nick > a").text(),
            parser.created,
            parser.commentCount,
            parser.rating,
            parser.myLike,
            document.select(".taglist a").map { it.text() },
            extractNumberFromEnd(document.id()).toLong(),
            document.select("span.favorite").size > 0,
            when (desc) {
                is TextNode -> desc.text()
                else -> ""
            }
        )
    }

    private fun extractNumberFromEnd(text: String): String {
        val m = Pattern.compile("\\d+$").matcher(text)
        if (!m.find()) throw IllegalStateException()
        return m.group()
    }

    private class ThumbnailParser(private val element: Element) {

        fun load(): Image? {
            val img = element.select("div.post_content img").first()
            if (img != null && img.hasAttr("width")) {
                if (img.attr("height").endsWith("%")) return null

                return Image(
                    if (hasFull(img))
                        img.parent().attr("href").replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
                    else
                        img.attr("src").replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2"),
                    Integer.parseInt(img.attr("width")),
                    Integer.parseInt(img.attr("height")))
            }
            return null
        }

        private fun hasFull(img: Element): Boolean {
            return "a" == img.parent().tagName()
        }
    }

    private class YoutubeThumbnailParser(private val element: Element) {

        fun load(): Image? {
            val iframe = element.select("iframe.youtube-player").first() ?: return null
            val m = YoutubeThumbnailParser.SRC_PATTERN.matcher(iframe.attr("src"))
            if (!m.find()) throw IllegalStateException(iframe.attr("src"))
            return Image(
                "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg",
                Integer.parseInt(iframe.attr("width")),
                Integer.parseInt(iframe.attr("height")))
        }

        companion object {

            private val SRC_PATTERN = Pattern.compile("/embed/([^\\?]+)")
        }
    }

    private class VideoThumbnailParser(private val element: Element) {

        fun load(): Image? {
            val video = element.select("video[poster]").first() ?: return null
            try {
                return Image(
                    element.select("span.video_gif_holder > a").first().attr("href").replace("(/post/).+(-)".toRegex(), "$1$2"),
                    Integer.parseInt(video.attr("width")),
                    Integer.parseInt(video.attr("height")))
            } catch (e: Exception) {
                println("ELEMENT | " + video)
                throw e
            }
        }
    }

    private class PostParser(private val element: Element) {

        val commentCount: Int
            get() {
                val e = element.select("a.commentnum").first()
                val m = COMMENT_COUNT_REGEX.matcher(e.text())
                if (!m.find()) throw IllegalStateException()
                return Integer.parseInt(m.group())
            }

        val rating: Float
            get() {
                val e = element.select("span.post_rating > span").first()
                val m = RATING_REGEX.matcher(e.text())
                return if (m.find()) java.lang.Float.parseFloat(m.group()) else 0f
            }

        val created: Date
            get() {
                val e = element.select("span.date > span")
                return Date(1000L * java.lang.Long.parseLong(e.attr("data-time")))
            }

        val myLike: MyLike
            get() {
                val e = element.select("span.post_rating > span").first()
                return LikeParser(e).myLike
            }

        companion object {

            private val COMMENT_COUNT_REGEX = Pattern.compile("\\d+")
            private val RATING_REGEX = Pattern.compile("[\\d\\.]+")
        }
    }
}