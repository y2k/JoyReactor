package y2k.joyreactor.services.requests

import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.model.*
import y2k.joyreactor.services.requests.parser.PostParser
import java.util.*
import java.util.regex.Pattern
import rx.Observable

/**
 * Created by y2k on 11/21/15.
 */
class PostRequest(
    private val httpClient: HttpClient,
    private val parser: PostParser) : Function1<Long, Observable<PostRequest.Response>> {

    override operator fun invoke(postId: Long) = ioObservable { request(postId.toString()) }

    fun request(postId: String): Response {
        val commentsRequest = PostCommentsRequest()
        val similarPosts = ArrayList<SimilarPost>()
        val attachments = ArrayList<Attachment>()

        val page = httpClient.getDocument(getPostUrl(postId))

        val postNode = page.select("div.postContainer").first()
        val post = parser.parse(postNode)

        commentsRequest.request(page, postId.toLong())

        for (e in page.select(".similar_post img")) {
            val similarPost = SimilarPost(
                postId.toLong(),
                Image(e.absUrl("src"), 0, 0),
                getPostId(e.parent().attr("href")).toLong())
            similarPosts.add(similarPost)
        }

        val imgElement = postNode.select("div.image > img")
        if (imgElement.size > 1)
            for (e in imgElement.subList(1, imgElement.size - 1)) {
                val a = Attachment(
                    postId.toLong(),
                    Image(e.absUrl("src"),
                        Integer.parseInt(e.attr("width")),
                        Integer.parseInt(e.attr("height"))))
                attachments.add(a)
            }

        return Response(post, commentsRequest.comments, similarPosts, attachments)
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