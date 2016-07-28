package y2k.joyreactor.services.requests

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.PostWithAttachments
import y2k.joyreactor.model.PostsWithNext
import java.util.regex.Pattern

/**
 * Created by y2k on 9/26/15.
 */
class PostsForTagRequest(
    private val httpClient: HttpClient,
    private val urlBuilder: UrlBuilder,
    private val parser: (Element) -> PostWithAttachments) :
    Function2<String, String?, CompletableFuture<PostsWithNext>> {

    override fun invoke(groupId: String, pageId: String?): CompletableFuture<PostsWithNext> {
        return runAsync {
            val url = urlBuilder.build(groupId, pageId)
            val doc = httpClient.getDocument(url)
            PostsWithNext(getPosts(doc), getNext(doc))
        }
    }

    private fun getPosts(doc: Document): List<Post> {
        return doc
            .select("div.postContainer")
            .map { parser(it).post }
    }

    private fun getNext(doc: Document): String? {
        return doc.select("a.next").first()
            ?.let { doc.select("a.next").first().attr("href").extractNumberFromEnd() }
    }

    private fun String.extractNumberFromEnd(): String {
        val m = Pattern.compile("\\d+$").matcher(this)
        if (!m.find()) throw IllegalStateException()
        return m.group()
    }
}