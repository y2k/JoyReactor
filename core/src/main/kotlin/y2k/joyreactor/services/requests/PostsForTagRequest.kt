package y2k.joyreactor.services.requests

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.requests.parser.PostParser
import java.util.regex.Pattern

/**
 * Created by y2k on 9/26/15.
 */
class PostsForTagRequest(
    private val httpClient: HttpClient,
    private val urlBuilder: UrlBuilder,
    private val parser: PostParser) {

    fun requestAsync(groupId: Group, pageId: String? = null): Observable<Data> {
        return Observable
            .fromCallable {
                val url = urlBuilder.build(groupId, pageId)
                val doc = httpClient.getDocument(url)

                val posts = doc
                    .select("div.postContainer")
                    .map { parser.parse(it) }

                val next = doc.select("a.next").first()
                Data(posts, next?.let { extractNumberFromEnd(next.attr("href")) })
            }
            .subscribeOn(Schedulers.io())
    }

    private fun extractNumberFromEnd(text: String): String {
        val m = Pattern.compile("\\d+$").matcher(text)
        if (!m.find()) throw IllegalStateException()
        return m.group()
    }

    class Data(val posts: List<Post>, val nextPage: String?)
}