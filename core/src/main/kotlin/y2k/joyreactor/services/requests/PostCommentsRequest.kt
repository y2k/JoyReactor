package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import y2k.joyreactor.Comment

import java.io.IOException
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by y2k on 9/29/15.
 */
class PostCommentsRequest {

    var comments: List<Comment> = ArrayList()
        private set

    fun request(doc: Document) {
        val result = ArrayList<Comment>()
        for (node in doc.select("div.comment")) {
            val comment = Comment()
            comment.text = node.select("div.txt > div").first().text()
            comment.userImage = node.select("img.avatar").attr("src")
            comment.id = (node.select("span.comment_rating").attr("comment_id")).toLong()
            comment.rating = java.lang.Float.parseFloat(node.select("span.comment_rating").text().trim { it <= ' ' })

            val parent = node.parent()
            if ("comment_list" == parent.className())
                comment.parentId = NumberExtractor[parent.id()]

            val imgElement = node.select("div.image > img").first()
            if (imgElement != null)
                comment.setAttachment(
                        clearImageUrl(imgElement.absUrl("src")),
                        Integer.parseInt(imgElement.attr("width")),
                        Integer.parseInt(imgElement.attr("height")))

            result.add(comment)
        }

        ChildrenCounter.compute(result)
        comments = result
    }

    private fun clearImageUrl(url: String): String {
        return url.replace("(/comment/).+(-\\d+\\.[\\w\\d]+)$".toRegex(), "$1$2")
    }

    private object NumberExtractor {

        internal val NUMBER = Pattern.compile("\\d+")

        internal operator fun get(text: String): Long {
            val m = NUMBER.matcher(text)
            return if (m.find()) m.group().toLong() else 0L
        }
    }

    private object ChildrenCounter {

        internal fun compute(comments: List<Comment>) {
            // TODO: оптимизировать
            for (i in 0..comments.size - 1 - 1) {
                val c = comments[i]
                for (n in i + 1..comments.size - 1)
                    if (comments[n].parentId == c.id) c.replies++
            }
        }
    }
}