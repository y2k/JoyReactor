package y2k.joyreactor.services.requests

import org.jsoup.nodes.Element
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.*
import java.util.*
import java.util.regex.Pattern

/**
 * Created by y2k on 11/21/15.
 */
class PostRequest(
    private val httpClient: HttpClient,
    private val parser: (Element) -> Pair<Post, List<Attachment>>) :
    Function1<Long, CompletableFuture<PostRequest.Response>> {

    override operator fun invoke(postId: Long) = runAsync { doRequest(postId.toString()) }

    @Deprecated("")
    fun request(postId: String): Response = doRequest(postId)

    private fun doRequest(postId: String): Response {
        val commentsRequest = PostCommentsRequest()
        val similarPosts = ArrayList<SimilarPost>()

        val page = httpClient.getDocument(getPostUrl(postId))

        val postNode = page.select("div.postContainer").first()
        val postWithAttachments = parser(postNode)

        commentsRequest.request(page, postId.toLong())

        for (e in page.select(".similar_post img")) {
            val similarPost = SimilarPost(
                postId.toLong(),
                Image(e.absUrl("src"), 0, 0),
                getPostId(e.parent().attr("href")).toLong())
            similarPosts.add(similarPost)
        }

        return Response(
            postWithAttachments.first, commentsRequest.comments,
            similarPosts, postWithAttachments.second)
    }

    private fun getPostId(href: String): String {
        val m = POST_ID.matcher(href)
        if (!m.find()) throw IllegalStateException()
        return m.group(1)
    }

    private fun getPostUrl(postId: String): String {
        return "http://anime.reactor.cc/post/" + postId // TODO:
    }

    data class Response(
        val post: Post,
        val comments: List<Comment>,
        val similarPosts: List<SimilarPost>,
        val attachments: List<Attachment>)

    companion object {

        private val POST_ID = Pattern.compile("/post/(\\d+)")
    }
}