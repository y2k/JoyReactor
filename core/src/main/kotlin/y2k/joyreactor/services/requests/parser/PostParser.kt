package y2k.joyreactor.services.requests.parser

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import y2k.joyreactor.common.flatMapPair
import y2k.joyreactor.common.join
import y2k.joyreactor.model.*
import java.util.*
import java.util.regex.Pattern

/**
 * Created by y2k on 5/29/16.
 */
class PostParser(
    private val parseLike: (Element) -> MyLike) :
    Function1<Element, PostWithAttachments> {

    override operator fun invoke(document: Element): PostWithAttachments {
        val id = extractNumberFromEnd(document.id()).toLong()

        val title = document.select("div.post_content > div > h3").first()
        val desc = title?.nextSibling()
        val attachments = getAttachments(document, id)

        val post = Post(
            title = title?.text() ?: "",
            image = attachments.firstOrNull()?.image,
            userImage = document.select("div.uhead_nick > img").attr("src"),
            userName = document.select("div.uhead_nick > a").text(),
            created = getCreated(document),
            commentCount = getCommentCount(document),
            rating = getRating(document),
            myLike = getMyLike(document),
            tags = document.select(".taglist a").map { it.text() }.let { TagList(it) },
            id = id,
            isFavorite = document.select("span.favorite").size > 0,
            description = when (desc) {
                is TextNode -> desc.text()
                else -> ""
            }
        )

        return PostWithAttachments(post, attachments.drop(1))
    }

    private fun getAttachments(document: Element, id: Long): List<Attachment> {
        return document
            .select("div.post_top")
            .join(listOf(
                ThumbnailParser(),
                YoutubeThumbnailParser(),
                VideoThumbnailParser()))
            .flatMapPair { e, parse -> parse(e) }
            .map { Attachment(id, it) }
    }

    private fun getCommentCount(element: Element): Int {
        val e = element.select("a.commentnum").first()
        val m = COMMENT_COUNT_REGEX.matcher(e.text())
        if (!m.find()) throw IllegalStateException()
        return Integer.parseInt(m.group())
    }

    private fun getRating(element: Element): Float {
        val e = element.select("span.post_rating > span").first()
        val m = RATING_REGEX.matcher(e.text())
        return if (m.find()) java.lang.Float.parseFloat(m.group()) else 0f
    }

    private fun getCreated(element: Element): Date {
        val e = element.select("span.date > span")
        return Date(1000L * java.lang.Long.parseLong(e.attr("data-time")))
    }

    private fun getMyLike(element: Element): MyLike {
        val e = element.select("span.post_rating > span").first()
        return parseLike(e)
    }

    private fun extractNumberFromEnd(text: String): String {
        val m = Pattern.compile("\\d+$").matcher(text)
        if (!m.find()) throw IllegalStateException()
        return m.group()
    }

    private class ThumbnailParser() : Function1<Element, List<Image>> {
        override fun invoke(element: Element): List<Image> {
            return element
                .select("div.post_content img")
                .filter { it != null && it.hasAttr("width") }
                .filterNot { it.attr("height").endsWith("%") }
                .map {
                    Image(
                        if (hasFull(it))
                            it.parent().attr("href").replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
                        else
                            it.attr("src").replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2"),
                        Integer.parseInt(it.attr("width")),
                        Integer.parseInt(it.attr("height")))
                }
        }

        private fun hasFull(img: Element): Boolean = "a" == img.parent().tagName()
    }

    private class YoutubeThumbnailParser() : Function1<Element, List<Image>> {

        override fun invoke(element: Element): List<Image> {
            return element
                .select("iframe.youtube-player")
                .map {
                    val m = YoutubeThumbnailParser.SRC_PATTERN.matcher(it.attr("src"))
                    if (!m.find()) throw IllegalStateException(it.attr("src"))
                    Image(
                        "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg",
                        Integer.parseInt(it.attr("width")),
                        Integer.parseInt(it.attr("height")))
                }
        }

        companion object {

            private val SRC_PATTERN = Pattern.compile("/embed/([^\\?]+)")
        }
    }

    private class VideoThumbnailParser() : Function1<Element, List<Image>> {

        override fun invoke(element: Element): List<Image> {
            return element
                .select("video[poster]")
                .map {
                    try {
                        Image(
                            element.select("span.video_gif_holder > a").first().attr("href").replace("(/post/).+(-)".toRegex(), "$1$2"),
                            Integer.parseInt(it.attr("width")),
                            Integer.parseInt(it.attr("height")))
                    } catch (e: Exception) {
                        println("ELEMENT | " + it)
                        throw e
                    }
                }
        }
    }

    companion object {

        private val COMMENT_COUNT_REGEX = Pattern.compile("\\d+")
        private val RATING_REGEX = Pattern.compile("[\\d\\.]+")
    }
}